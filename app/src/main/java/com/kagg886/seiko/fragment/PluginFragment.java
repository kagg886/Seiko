package com.kagg886.seiko.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.net.UriKt;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.PluginAdapter;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PluginFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    private SwipeRefreshLayout layout;

    private FloatingActionButton button;
    private PluginAdapter adapter;
    private AlertDialog dialog;

    private final ActivityResultLauncher<Intent> getFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (r) -> {
        Optional.ofNullable(r.getData()).ifPresent((i) -> {
            Uri uri = i.getData();
            String fileName = UUID.randomUUID().toString().replace("-", "") + ".apk";
            Optional.ofNullable(getActivity()).ifPresent((activity) -> {
                File pluginDir = activity.getExternalFilesDir("plugin");
                Path target = Paths.get(pluginDir.getAbsolutePath(), fileName);
                try {
                    Files.copy(getActivity().getContentResolver().openInputStream(uri), target);
                    activity.runOnUiThread(() -> {
                        BotRunnerService.INSTANCE.getSeikoPluginList().refresh();
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackBroadCast.sendBroadCast("导入失败");
                }
            });
        });
    });

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 2:
                    PluginFragment.this.dialog.show();
                    break;
                case 0:
                    SnackBroadCast.sendBroadCast("下载完成");
                    PluginFragment.this.dialog.dismiss();
                    break;
                case 1:
                    SnackBroadCast.sendBroadCast("发生异常:" + ((Throwable) msg.getData().getSerializable("exception")).getMessage());
                    PluginFragment.this.dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("您要...")
                .setItems(
                    new String[]{"从本地导入", "从网络导入"}, (dialog1, which) -> {
                        if (which == 0)
                            localImportPlugin();
                        else if (which == 1)
                            networkImportPlugin();
                    }
                ).create();
        dialog.show();
    }

    public void downloadPlugin(String url) {
        PluginFragment.this.dialog = new AlertDialog.Builder(getActivity())
                .setTitle("下载中...")
                .setCancelable(false)
                .setMessage("请稍等片刻...")
                .create();
        mHandler.sendEmptyMessage(2);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request req = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                notifyError(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                File file = null;
                try {
                    byte[] data = response.body().source().readByteArray();
                    File pluginDir = getActivity().getExternalFilesDir("plugin");
                    String fileName = UUID.randomUUID().toString().replace("-", "") + ".apk";
                    file = Paths.get(pluginDir.getAbsolutePath(), fileName).toFile();
                    IOUtil.writeByteToFile(file.getAbsolutePath(), data);
                    getActivity().runOnUiThread(() -> {
                        BotRunnerService.INSTANCE.getSeikoPluginList().refresh();
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    // 下载失败，删除文件
                    if (file != null && file.exists()) {
                        if (SeikoApplication.globalConfig.getBoolean("badPluginAutoDel", true)) {
                            file.delete();
                        }
                    }
                    notifyError(e);
                    return;
                }
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    public void notifyError(Throwable e) {
        Message m = new Message();
        m.what = 1;
        Bundle b = new Bundle();
        b.putSerializable("exception", e);
        m.setData(b);
        mHandler.sendMessage(m);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plugin, container, false);

        listView = v.findViewById(R.id.fragment_plugin_list);
        adapter = new PluginAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(((MainActivity) getActivity()));
            SeikoPlugin desc = BotRunnerService.INSTANCE.getSeikoPluginList().get(position);
            builder.setTitle("操作:" + desc.getDescription().getName());
            builder.setItems(new String[]{"删除插件"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        if (desc.getFile() == null) {
                            SnackBroadCast.sendBroadCast("内置插件不可删除");
                            return;
                        }
                        desc.getFile().delete();
                        BotRunnerService.INSTANCE.getSeikoPluginList().remove(desc);
                        adapter.notifyDataSetChanged();
                        SnackBroadCast.sendBroadCast("删除成功!");
                    }
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

    public AlertDialog networkImportPluginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.get_plugin_download_url);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_import_plugin, null);
        EditText edt = view.findViewById(R.id.dialog_importPluginUrl);
        builder.setView(view);
        builder.setPositiveButton("确定", (dialog, which) -> {
            String url = edt.getText().toString();
            if (url.trim().length() == 0 || !(url.startsWith("http://") || url.startsWith("https://"))) {
                SnackBroadCast.sendBroadCast("请正确填写链接");
                return;
            }
            downloadPlugin(url);
        });
        builder.setNegativeButton("取消", (dialog, which) -> {});
        return builder.create();
    }

    public void networkImportPlugin() {
        networkImportPluginDialog().show();
    }

    public void localImportPlugin() {
        // 选择文件
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);


        getFileLauncher.launch(Intent.createChooser(intent, "选择导入的插件"));
    }

    @Override
    public void onRefresh() {
        //TODO 此处刷新一次则伪代码会刷新两次，知道原因但是不知道怎么解，求助
        if (BotRunnerService.INSTANCE == null) {
            SnackBroadCast.sendBroadCast("服务启动中，请稍等片刻");
            layout.setRefreshing(false);
            return;
        }
        BotRunnerService.INSTANCE.getSeikoPluginList().refresh();
        adapter.notifyDataSetChanged();
        layout.setRefreshing(false);
    }
}