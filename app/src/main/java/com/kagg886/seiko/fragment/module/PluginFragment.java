package com.kagg886.seiko.fragment.module;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.PluginAdapter;
import com.kagg886.seiko.plugin.PluginList;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class PluginFragment extends Fragment implements View.OnClickListener {
    private ListView listView;
    private PluginAdapter adapter;
    private FloatingActionButton button;

    private static ActivityResultLauncher<Intent> readCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plugin, container, false);

        listView = v.findViewById(R.id.fragment_plugin_list);
        adapter = new PluginAdapter((MainActivity) getActivity());
        listView.setAdapter(adapter);
        button = v.findViewById(R.id.fragment_plugin_menu);
        button.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        BotRunnerService.INSTANCE.getSeikoPluginList().refresh();
        adapter.notifyDataSetChanged();
        ((MainActivity) getActivity()).snack("刷新完成");
    }
}