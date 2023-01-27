package com.kagg886.seiko.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.DICAdapter;
import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.event.SnackBroadCast;
import org.jetbrains.annotations.NotNull;

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

    private final MainActivity avt;

    public DICFragment(MainActivity avt) {
        super();
        this.avt = avt;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plugin, container, false);
        listView = v.findViewById(R.id.fragment_plugin_list);
        adapter = new DICAdapter(avt);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(avt);
                DictionaryFile file = DICList.getInstance().get(position);
                builder.setTitle("操作:" + file.getName());
                builder.setItems(new String[]{"删除词库"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            file.getFile().delete();
                            adapter.notifyDataSetChanged();
                            SnackBroadCast.sendBroadCast(avt, "删除成功!");
                        }
                    }
                });
                builder.create().show();
            }
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
                .setTitle("您要...").setItems(new String[]{"导入词库"}, (dialog1, which) -> {
                    switch (which) {
                        case 0:
                            SnackBroadCast.sendBroadCast(avt, "懒得做了xwx\n等更新吧www");
                            break;
                        case 1:
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
