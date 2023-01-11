package com.kagg886.seiko.fragment.module;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.PluginAdapter;
import com.kagg886.seiko.service.BotRunnerService;

import com.kagg886.seiko.util.FileUtil;
import com.kagg886.seiko.util.NetUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import kotlin.text.StringsKt;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PluginFragment extends Fragment implements View.OnClickListener {
    private static ActivityResultLauncher<Intent> readCall;
    private ListView listView;
    private PluginAdapter adapter;
    private FloatingActionButton button;
    private AlertDialog dialog;

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
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("您要...").setItems(new String[]{"导入插件", "刷新插件"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                importPluginDialog().show();
                                break;
                            case 1:
                                BotRunnerService.INSTANCE.getSeikoPluginList().refresh();
                                adapter.notifyDataSetChanged();
                                ((MainActivity) getActivity()).snack("刷新完成");
                                break;
                        }
                    }
                }).create();
        dialog.show();
    }

    public void downloadPlugin(MainActivity avt, String url) {
        PluginFragment.this.dialog = new AlertDialog.Builder(avt)
                .setTitle("下载中...")
                .setCancelable(false)
                .setMessage("请稍等片刻...")
                .create();
        mHandler.sendEmptyMessage(2);
        NetUtil.downloadFromUrlAsync(url, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                File file = null;
                try {
                    byte[] data = response.body().source().readByteArray();
                    File pluginDir = avt.getExternalFilesDir("plugin");
                    String fileName = UUID.randomUUID().toString().replace("-", "") + ".apk";
                    file = Paths.get(pluginDir.getAbsolutePath(), fileName).toFile();
                    FileUtil.saveFile(avt, data, file);
                    avt.runOnUiThread(() -> {
                        BotRunnerService.INSTANCE.getSeikoPluginList().refresh();
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    // 下载失败，删除文件
                    if(file != null && file.exists()) {
                        file.delete();
                    }
                    notifyError(e);
                    return;
                }
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                notifyError(e);
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

    public AlertDialog importPluginDialog() {
        MainActivity avt = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(avt);
        View view = LayoutInflater.from(avt).inflate(R.layout.dialog_import_plugin, null);
        builder.setView(view);
        builder.setPositiveButton("确定", (dialog, which) -> {
            TextInputLayout importPluginUrl = view.findViewById(R.id.dialog_importPluginUrl);
            String url = importPluginUrl.getEditText().getText().toString();
            if(url.trim().length() == 0 || !(url.startsWith("http://") || url.startsWith("https://"))) {
                avt.snack("请正确填写链接");
                return;
            }
            downloadPlugin(avt, url);
        });
        return builder.create();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity avt = (MainActivity) getActivity();
            switch (msg.what) {
                case 2:
                    PluginFragment.this.dialog.show();
                    break;
                case 0:
                    avt.snack("下载完成");
                    PluginFragment.this.dialog.dismiss();
                    break;
                case 1:
                    avt.snack("发生异常:" + ((Throwable) msg.getData().getSerializable("exception")).getMessage());
                    PluginFragment.this.dialog.dismiss();
                    break;
            }
        }
    };
}