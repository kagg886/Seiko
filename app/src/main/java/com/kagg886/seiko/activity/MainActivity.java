package com.kagg886.seiko.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.kagg886.seiko.BuildConfig;
import com.kagg886.seiko.R;
import com.kagg886.seiko.adapter.ModuleAdapter;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.fragment.DICFragment;
import com.kagg886.seiko.fragment.LoginFragment;
import com.kagg886.seiko.fragment.PluginFragment;
import com.kagg886.seiko.fragment.SettingsFragment;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.ShareUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.activity
 * @className: MainActivity
 * @author: kagg886
 * @description: 主活动
 * @date: 2022/12/12 18:55
 * @version: 1.0
 */
public class MainActivity extends AppCompatActivity {

    private LinearLayout rootView;
    private TabLayout layout;
    private ViewPager pager;
    private ModuleAdapter adapter;

    private ActivityResult result;

    public ActivityResultLauncher<Intent> verifyCall = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> MainActivity.this.result = result);
    private DialogBroadCast dialogBroadCast;
    private SnackBroadCast snackBroadCast;

    public ActivityResult getResult() {
        while (result == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        ActivityResult p = result;
        result = null;
        return p;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(dialogBroadCast);
        unregisterReceiver(snackBroadCast);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检测崩溃报告
        File f = getExternalFilesDir("crash");
        int crashFileCount = f.listFiles().length;
        if (crashFileCount > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(String.format(getText(R.string.crash_found).toString(), crashFileCount));
            builder.setMessage(R.string.crash_action);
            builder.setPositiveButton(R.string.crash_action_share, (dialog, which) -> {
                File crashFile = getExternalFilesDir("tmp").toPath().resolve("crash.zip").toFile();
                if (crashFile.exists()) {
                    crashFile.delete();
                }
                try {
                    crashFile.createNewFile();
                    ZipOutputStream stream = new ZipOutputStream(Files.newOutputStream(crashFile.toPath()));
                    for (File log : f.listFiles()) {
                        stream.putNextEntry(new ZipEntry(log.getName()));
                        stream.write(IOUtil.loadByteFromFile(log.getAbsolutePath()));
                    }
                    stream.close();
                    IOUtil.delFile(f);
                    ShareUtil.quickShare(MainActivity.this, crashFile, "*/*");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            builder.setNegativeButton(R.string.crash_action_delete_all, (dialog, which) -> {
                IOUtil.delFile(f);
                SnackBroadCast.sendBroadCast(R.string.crash_action_deleted);
            });
            builder.create().show();
        }

        //初始化Dialog广播
        dialogBroadCast = new DialogBroadCast(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DialogBroadCast.TAG);
        registerReceiver(dialogBroadCast, filter);

        //初始化SnackBar广播
        rootView = findViewById(R.id.activity_main_root);
        snackBroadCast = new SnackBroadCast(rootView);
        filter = new IntentFilter();
        filter.addAction(SnackBroadCast.TAG);
        registerReceiver(snackBroadCast, filter);

        layout = findViewById(R.id.activity_main_view_tab_layout);
        pager = findViewById(R.id.activity_main_view_view_pager);
        adapter = new ModuleAdapter(getSupportFragmentManager());

        //启动Seiko托管服务，它是整个程序运行的关键
        Intent a = new Intent(this, BotRunnerService.class);
        if (BotRunnerService.INSTANCE == null) {
            checkUpdate();
            startForegroundService(a);
        }

        //填充列表
        ArrayList<ModuleAdapter.Structure> fragments = new ArrayList<>();
        addFragment(fragments, R.string.tab_bot_list, new LoginFragment());
        addFragment(fragments, R.string.tab_plugin_list, new PluginFragment());
        addFragment(fragments, R.string.tab_dic_list, new DICFragment());
        addFragment(fragments, R.string.tab_settings, new SettingsFragment());

        adapter.setViews(fragments);
        pager.setAdapter(adapter);
        layout.setupWithViewPager(pager);
    }

    private void addFragment(List<ModuleAdapter.Structure> fragments, @StringRes int str, Fragment fragment) {
        fragments.add(new ModuleAdapter.Structure(getText(str).toString(), fragment));
    }

    private void checkUpdate() {
        new Thread(() -> {
            try {
                JSONObject object = new JSONObject(
                        Jsoup.connect("https://api.github.com/repos/kagg886/Seiko/releases/latest")
                                .ignoreContentType(true)
                                .timeout(10000)
                                .execute().body());
                String newVer = object.optString("tag_name");
                if (!BuildConfig.VERSION_NAME.equals(newVer)) {
                    Message message = new Message();
                    message.what = 0;
                    message.getData().putString("update", object.toString());
                    checkHandler.sendMessage(message);
                }
            } catch (Exception e) {
                SnackBroadCast.sendBroadCast(R.string.update_failed);
            }
        }).start();
    }

    private Handler checkHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    JSONObject data;
                    try {
                        data = new JSONObject(msg.getData().getString("update"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    String body = data.optString("body");
                    String title = data.optString("tag_name");
                    builder.setTitle(title);
                    builder.setMessage(body.substring(0, Math.min(body.length()-1, 500)) + (body.length()-1 > 500 ? "..." : ""));
                    builder.setPositiveButton("通过Github打开",(dialog,which) -> {
                        ShareUtil.openUrlByBrowser(data.optString("html_url"));
                    });
                    builder.setNegativeButton("通过Gitee打开",(dialog, which) -> {
                        ShareUtil.openUrlByBrowser("https://gitee.com/kagg886/Seiko/releases/tag/" + title);
                    });
                    builder.create().show();
                    break;
                case 1:
                    break;
            }
        }
    };
}