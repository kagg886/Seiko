package com.kagg886.seiko.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kagg886.seiko.R;
import com.kagg886.seiko.adapter.LogAdapter;
import com.kagg886.seiko.util.IOUtil;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView log;
    private LogAdapter adapter;
    private FloatingActionButton btn;

    private ActivityResultLauncher<Intent> writeCall;

    private String nowBot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_plugin);
        nowBot = getIntent().getStringExtra("uin");

        File logFile = IOUtil.newFile(getExternalFilesDir("bots"),"/",nowBot,"/log/",new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()),".log");
        if (!logFile.exists()) {
            Toast.makeText(this,"今日未启动bot!",Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        log = findViewById(R.id.fragment_plugin_list);

        adapter = new LogAdapter(this,logFile);
        log.setAdapter(adapter);

        btn = findViewById(R.id.fragment_plugin_menu);
        btn.setOnClickListener(this);

        registerActivityResultLauncher();

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "log-" + nowBot + ".zip");
        writeCall.launch(intent);
    }

    public void snack(String text) {
        Snackbar.make(log, text, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    private void registerActivityResultLauncher() {
        writeCall = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() == null) {
                return;
            }
            try {
                File p = new File(getExternalFilesDir("bots") + "/" + nowBot + "/log");
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
    }
}