package com.kagg886.seiko.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.activity.DICEditActivity;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.DICAdapter;
import com.kagg886.seiko.constant.GlobalConstant;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.ShareUtil;
import io.github.seikodictionaryenginev2.base.entity.DictionaryFile;
import io.github.seikodictionaryenginev2.base.entity.DictionaryProject;
import io.github.seikodictionaryenginev2.base.env.DICList;
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
            DictionaryProject file = DICList.INSTANCE.get(position);
            builder.setTitle(text(R.string.dic_edit_title, file.getName()));
            builder.setItems(new String[] { text(R.string.dic_edit_action_edit), text(R.string.dic_edit_action_delete)}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        openDICCodeEditor(true, file.getIndexFile().getFile().getAbsolutePath());
                        break;
                    case 1:
//                        file.getFile().delete();
                        IOUtil.delFile(file.isSimpleDictionary() ? file.getIndexFile().getFile() : file.getIndexFile().getFile().getParentFile());
                        adapter.notifyDataSetChanged();
                        SnackBroadCast.sendBroadCast(R.string.dic_edit_action_delete_success);
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
                .setTitle(R.string.dic_title)
                .setItems(new String[] {text(R.string.dic_action_create), text(R.string.dic_action_import), text(R.string.dic_action_github), text(R.string.dic_action_gitee)}, (dialog1, which) -> {
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
                                        builder.setTitle(R.string.dic_action_import_name);
                                        View v = LayoutInflater.from(avt).inflate(R.layout.ask_dic_name, null);
                                        EditText edt = v.findViewById(R.id.dialog_dicName);
                                        builder.setView(v);
                                        builder.setPositiveButton(R.string.ok, (dialog2, which1) -> {
                                            String txt = edt.getText().toString();
                                            txt = (TextUtils.isEmpty(txt) ? UUID.randomUUID().toString().replace("-", "").substring(0, 8) : txt) + GlobalConstant.dicFileExt;
                                            try {
                                                String s = IOUtil.loadStringFromStream(avt.getContentResolver().openInputStream(result.getData().getData()));
                                                IOUtil.writeStringToFile(avt.getExternalFilesDir("dic").toPath().resolve(txt).toFile().getAbsolutePath(), s);
                                                SnackBroadCast.sendBroadCast(R.string.dic_action_import_success);
                                                adapter.notifyDataSetChanged();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                                        builder.setNegativeButton(R.string.cancel, (dialog2, which2) -> {});
                                        builder.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        SnackBroadCast.sendBroadCast(R.string.dic_action_import_fail);
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
    private String text(@StringRes int s, Object... args) {
        return String.format(SeikoApplication.getSeikoApplicationContext().getText(s).toString(), args);
    }
}
