package com.kagg886.seiko.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.adapter.BotAdapter;
import com.kagg886.seiko.adapter.ModuleAdapter;
import com.kagg886.seiko.fragment.module.LoginFragment;
import com.kagg886.seiko.fragment.module.PluginFragment;
import com.kagg886.seiko.fragment.module.SettingsFragment;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public ActivityResultLauncher<Intent> writeCall = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() == null) {
            return;
        }
        try {
            File p = new File(getExternalFilesDir("bots") + "/" + BotAdapter.chooseUin + "/log");
            if (!p.isDirectory()) {
                snack("并没有日志，不需要导出");
                return;
            }
            OutputStream stream = getContentResolver().openOutputStream(result.getData().getData());
            ZipOutputStream output = new ZipOutputStream(stream);
            for (File a : p.listFiles()) {
                output.putNextEntry(new ZipEntry(a.getName()));
                output.write(IOUtil.loadByteFromFile(a.getAbsolutePath()));
            }
            output.close();
            stream.close();
            snack("导出成功!");
        } catch (Exception e) {
            Log.w("DEBUG",e);
            snack("导出失败!");
        }
    });

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.activity_main_view_tab_layout);
        pager = findViewById(R.id.activity_main_view_view_pager);
        adapter = new ModuleAdapter(getSupportFragmentManager());
        rootView = findViewById(R.id.activity_main_root);
        ArrayList<ModuleAdapter.Structure> fragments = new ArrayList<>();
        fragments.add(new ModuleAdapter.Structure("BOT列表", new LoginFragment()));
        fragments.add(new ModuleAdapter.Structure("插件", new PluginFragment()));
        fragments.add(new ModuleAdapter.Structure("设置", new SettingsFragment()));
        adapter.setViews(fragments);
        pager.setAdapter(adapter);
        layout.setupWithViewPager(pager);

        Intent a = new Intent(this, BotRunnerService.class);
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BotRunnerService.INSTANCE = ((BotRunnerService.Bridge) service).getService();
                BotRunnerService.INSTANCE.setActivity(MainActivity.this);
                snack("已绑定服务");
                Log.i("DEBUG", "Service Connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.w("DEBUG", "Service DisConnected");
                BotRunnerService.INSTANCE = null;
            }
        };
        if (BotRunnerService.INSTANCE == null) {
            bindService(a, conn, BIND_AUTO_CREATE);
        } else {
            BotRunnerService.INSTANCE.setActivity(MainActivity.this);
            snack("已重绑服务");
        }
    }

    public void snack(String text) {
        Snackbar.make(rootView, text, BaseTransientBottomBar.LENGTH_LONG).show();
    }
}