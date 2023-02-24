package com.kagg886.seiko.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.DICAdapter;
import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.util.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
            builder.setItems(new String[]{"删除伪代码"}, (dialog, which) -> {
                if (which == 0) {
                    file.getFile().delete();
                    adapter.notifyDataSetChanged();
                    SnackBroadCast.sendBroadCast("删除成功!");
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
                .setTitle("您要...").setItems(new String[]{"导入伪代码"}, (dialog1, which) -> {
                    switch (which) {
                        case 0:
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
                                            String txt = (TextUtils.isEmpty(edt.getEditText().getText().toString()) ? UUID.randomUUID().toString().replace("-", "") : edt.getEditText().getText().toString()) + ".txt";
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
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        layout.setRefreshing(false);
    }
}
