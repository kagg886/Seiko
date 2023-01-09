package com.kagg886.seiko.fragment.module;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.DICAdapter;
import com.kagg886.seiko.adapter.PluginAdapter;
import com.kagg886.seiko.service.BotRunnerService;
import org.jetbrains.annotations.NotNull;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.fragment.module
 * @className: DICFragment
 * @author: kagg886
 * @description: 提供简易的Dictionary管理
 * @date: 2023/1/9 18:48
 * @version: 1.0
 */
public class DICFragment extends Fragment implements View.OnClickListener {
    private ListView listView;
    private DICAdapter adapter;
    private FloatingActionButton button;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plugin, container, false);
        listView = v.findViewById(R.id.fragment_plugin_list);
        adapter = new DICAdapter(((MainActivity)getActivity()));
        listView.setAdapter(adapter);
        button = v.findViewById(R.id.fragment_plugin_menu);
        button.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("您要...").setItems(new String[]{"导入词库", "重载词库"}, (dialog1, which) -> {
                    switch (which) {
                        case 0:
                            ((MainActivity) getActivity()).snack("懒得做了xwx\n等更新吧www");
                            break;
                        case 1:
                            adapter.notifyDataSetChanged();
                            ((MainActivity) getActivity()).snack("刷新完成");
                            break;
                    }
                }).create();
        dialog.show();
    }
}
