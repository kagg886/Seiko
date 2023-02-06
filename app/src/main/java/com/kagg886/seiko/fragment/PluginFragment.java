package com.kagg886.seiko.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.PluginAdapter;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;


public class PluginFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    private SwipeRefreshLayout layout;

    private FloatingActionButton button;
    private PluginAdapter adapter;
    private AlertDialog dialog;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 2:
                    PluginFragment.this.dialog.show();
                    break;
                case 0:
                    SnackBroadCast.sendBroadCast(getContext(), "下载完成");
                    PluginFragment.this.dialog.dismiss();
                    break;
                case 1:
                    SnackBroadCast.sendBroadCast(getContext(), "发生异常:" + ((Throwable) msg.getData().getSerializable("exception")).getMessage());
                    PluginFragment.this.dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("您要...").setItems(new String[]{"从网络导入"}, (dialog1, which) -> {
                    if (which == 0) {
                        importPluginDialog().show();
                    }
                }).create();
        dialog.show();
    }

    public void downloadPlugin(MainActivity avt, String url) {
        PluginFragment.this.dialog = new AlertDialog.Builder(((MainActivity) getActivity()))
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
                    File pluginDir = ((MainActivity) getActivity()).getExternalFilesDir("plugin");
                    String fileName = UUID.randomUUID().toString().replace("-", "") + ".apk";
                    file = Paths.get(pluginDir.getAbsolutePath(), fileName).toFile();
                    IOUtil.writeByteToFile(file.getAbsolutePath(), data);
                    ((MainActivity) getActivity()).runOnUiThread(() -> {
                        BotRunnerService.INSTANCE.getSeikoPluginList().refresh();
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    // 下载失败，删除文件
                    if (file != null && file.exists()) {
                        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("badPluginAutoDel", true)) {
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
        adapter = new PluginAdapter(((MainActivity) getActivity()));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(((MainActivity) getActivity()));
                SeikoPlugin desc = BotRunnerService.INSTANCE.getSeikoPluginList().get(position);
                builder.setTitle("操作:" + desc.getDescription().getName());
                builder.setItems(new String[]{"删除插件"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (desc.getFile() == null) {
                                SnackBroadCast.sendBroadCast(((MainActivity) getActivity()), "内置插件不可删除");
                                return;
                            }
                            desc.getFile().delete();
                            BotRunnerService.INSTANCE.getSeikoPluginList().remove(desc);
                            adapter.notifyDataSetChanged();
                            SnackBroadCast.sendBroadCast(((MainActivity) getActivity()), "删除成功!");
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

    public AlertDialog importPluginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(((MainActivity) getActivity()));
        View view = LayoutInflater.from(((MainActivity) getActivity())).inflate(R.layout.dialog_import_plugin, null);
        builder.setView(view);
        builder.setPositiveButton("确定", (dialog, which) -> {
            TextInputLayout importPluginUrl = view.findViewById(R.id.dialog_importPluginUrl);
            String url = importPluginUrl.getEditText().getText().toString();
            if (url.trim().length() == 0 || !(url.startsWith("http://") || url.startsWith("https://"))) {
                SnackBroadCast.sendBroadCast(((MainActivity) getActivity()), "请正确填写链接");
                return;
            }
            downloadPlugin(((MainActivity) getActivity()), url);
        });
        return builder.create();
    }

    @Override
    public void onRefresh() {
        //TODO 此处刷新一次则伪代码会刷新两次，知道原因但是不知道怎么解，求助
        if (BotRunnerService.INSTANCE == null) {
            SnackBroadCast.sendBroadCast(((MainActivity) getActivity()), "服务启动中，请稍等片刻");
            layout.setRefreshing(false);
            return;
        }
        BotRunnerService.INSTANCE.getSeikoPluginList().refresh();
        adapter.notifyDataSetChanged();
        layout.setRefreshing(false);
    }
}