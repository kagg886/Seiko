package com.kagg886.seiko.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.DICEditActivity;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.DICAdapter;
import com.kagg886.seiko.constant.GlobalConstant;
import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.ShareUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.fragment.module
 * @className: DICFragment
 * @author: kagg886
 * @description: 提供简易的伪代码文件管理
 * @date: 2023/1/9 18:48
 * @version: 1.0
 */
public class DICFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    private SwipeRefreshLayout layout;
    private DICAdapter adapter;
    private FloatingActionButton button;

    private final ActivityResultLauncher<Intent> launchDICEditor = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (r) -> {
        adapter.notifyDataSetChanged();
    });

    public DICFragment() {
        super();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plugin, container, false);
        listView = v.findViewById(R.id.fragment_plugin_list);
        adapter = new DICAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            DictionaryFile file = DICList.getInstance().get(position);
            builder.setTitle("操作:" + file.getName());
            builder.setItems(new String[]{"编辑伪代码", "删除伪代码"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        openDICCodeEditor(true, file.getName());
                        break;
                    case 1:
                        file.getFile().delete();
                        adapter.notifyDataSetChanged();
                        SnackBroadCast.sendBroadCast("删除成功!");
                        break;
                }
            });
            builder.create().show();
        });
        button = v.findViewById(R.id.fragment_plugin_menu);
        button.setOnClickListener(this);
        layout = v.findViewById(R.id.fragment_plugin_refresh);
        layout.setOnRefreshListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("您要...").setItems(new String[]{"新建伪代码", "导入伪代码","查看在线教程(可能需要翻墙)","查看在线教程(无需翻墙)"}, (dialog1, which) -> {
                    switch (which) {
                        case 0:
                            // 新建伪代码 / 打开编辑器
                            openDICCodeEditor(false, null);
                            break;
                        case 1:
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");//无类型限制
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            MainActivity avt = ((MainActivity) requireActivity());
                            avt.verifyCall.launch(intent);
                            new Thread(() -> {
                                ActivityResult result = ((MainActivity) requireActivity()).getResult();
                                if (result.getData() == null) {
                                    return;
                                }
                                avt.runOnUiThread(() -> {
                                    try {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(avt);
                                        builder.setCancelable(false);
                                        View v = LayoutInflater.from(avt).inflate(R.layout.dialog_import_plugin, null);
                                        TextInputLayout edt = v.findViewById(R.id.dialog_importPluginUrl);
                                        edt.setHint("为导入的词库命名(随机命名则为空)");
                                        builder.setView(v);
                                        builder.setPositiveButton("确定", (dialog2, which1) -> {
                                            String txt = (TextUtils.isEmpty(edt.getEditText().getText().toString()) ? UUID.randomUUID().toString().replace("-", "").substring(0, 8) : edt.getEditText().getText().toString()) + GlobalConstant.dicFileExt;
                                            try {
                                                String s = IOUtil.loadStringFromStream(avt.getContentResolver().openInputStream(result.getData().getData()));
                                                IOUtil.writeStringToFile(avt.getExternalFilesDir("dic").toPath().resolve(txt).toFile().getAbsolutePath(), s);
                                                SnackBroadCast.sendBroadCast("导入成功!");
                                                adapter.notifyDataSetChanged();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                                        builder.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        SnackBroadCast.sendBroadCast("导入失败!");
                                    }
                                });
                            }).start();
                            break;
                        case 2:
                            ShareUtil.openUrlByBrowser("https://github.com/kagg886/Seiko/blob/master/DictionaryCore/README.md");
                            break;
                        case 3:
                            ShareUtil.openUrlByBrowser("https://gitee.com/kagg886/Seiko/blob/master/DictionaryCore/README.md");
                            break;
                    }
                }).create();
        dialog.show();
    }

    private void openDICCodeEditor(boolean exist, String filename) {
        // 弹出编辑页面
        final Intent intent = new Intent(getActivity(), DICEditActivity.class);
        intent.putExtra("exist_file", exist);
        intent.putExtra("filename", filename);
        launchDICEditor.launch(intent);
    }


    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        layout.setRefreshing(false);
    }
}
