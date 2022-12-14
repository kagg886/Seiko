package com.kagg886.seiko.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.adapter.ModuleAdapter;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.fragment.module.DICFragment;
import com.kagg886.seiko.fragment.module.LoginFragment;
import com.kagg886.seiko.fragment.module.PluginFragment;
import com.kagg886.seiko.fragment.module.SettingsFragment;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

    private DialogBroadCast dialogBroadCast;

    @Override
    protected void onDestroy() {
        unregisterReceiver(dialogBroadCast);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File f = getExternalFilesDir("crash");
        if (f.listFiles().length != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("发现了" + f.listFiles().length + "个崩溃文件");
            builder.setMessage("请在下方选择你的操作");
            builder.setPositiveButton("打包并分享", (dialog, which) -> {
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
                    IOUtil.quickShare(MainActivity.this, crashFile, "*/*");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            builder.setNegativeButton("全部删除", (dialog, which) -> {
                IOUtil.delFile(f);
                snack("已全部删除!");
            });
            builder.create().show();
        }

        dialogBroadCast = new DialogBroadCast(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DialogBroadCast.TAG);
        registerReceiver(dialogBroadCast, filter);

        layout = findViewById(R.id.activity_main_view_tab_layout);
        pager = findViewById(R.id.activity_main_view_view_pager);
        adapter = new ModuleAdapter(getSupportFragmentManager());
        rootView = findViewById(R.id.activity_main_root);

        Intent a = new Intent(this, BotRunnerService.class);
        BotRunnerService.avt = this;
        if (BotRunnerService.INSTANCE == null) {
            startForegroundService(a);
            snack("绑定服务成功");
        }

        ArrayList<ModuleAdapter.Structure> fragments = new ArrayList<>();
        fragments.add(new ModuleAdapter.Structure("BOT列表", new LoginFragment()));
        fragments.add(new ModuleAdapter.Structure("插件", new PluginFragment()));
        fragments.add(new ModuleAdapter.Structure("词库",new DICFragment()));
        fragments.add(new ModuleAdapter.Structure("设置", new SettingsFragment()));
        adapter.setViews(fragments);
        pager.setAdapter(adapter);
        layout.setupWithViewPager(pager);

    }

    public void snack(String text) {
        Snackbar.make(rootView, text, BaseTransientBottomBar.LENGTH_LONG).show();
    }
}