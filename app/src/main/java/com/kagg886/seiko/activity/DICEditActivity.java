package com.kagg886.seiko.activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.adapter.DICAdapter;
import com.kagg886.seiko.constant.GlobalConstant;
import com.kagg886.seiko.util.IOUtil;

import java.io.IOException;
import java.util.function.Consumer;
import io.github.rosemoe.sora.widget.CodeEditor;

public class DICEditActivity extends AppCompatActivity {
    private CodeEditor code;
    private Button saveCodeBtn;
    private TextView filenameView;

    private Boolean existFile = false;

    private String filename = null;

    private static DICAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dic_edit);
        code = findViewById(R.id.code);
        saveCodeBtn = findViewById(R.id.save_code_btn);
        filenameView = findViewById(R.id.filename);

        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bindListeners();
    }

    private void init() throws IOException {
        saveCodeBtn.setActivated(false);
        existFile = getIntent().getBooleanExtra("exist_file", false);
        if (existFile)
            saveCodeBtn.setText("修改");
        else
            saveCodeBtn.setText("创建");
        filename = getIntent().getStringExtra("filename");
        if (filename == null) {
            filenameView.setText(R.string.code_file_name_init);
        } else {
            filenameView.setText(filename);
            // 从文件读取出来
            Uri dicFileUri = Uri.fromFile(this.getExternalFilesDir("dic").toPath().resolve(filename).toFile());
            String content = IOUtil.loadStringFromStream(this.getContentResolver().openInputStream(dicFileUri));
            code.setText(content);
        }
    }

    private void askFilename(Consumer<String> stringConsumer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.input_dic_name);
        View v = LayoutInflater.from(this).inflate(R.layout.ask_dic_name, null);
        EditText edt = v.findViewById(R.id.dialog_dicName);
        builder.setView(v);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String name = edt.getText().toString();
            if (TextUtils.isEmpty(name)) {
                toast("文件名不可为空");
            } else {
                stringConsumer.accept(name);
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> {});
        builder.show();
    }

    private void toast(String msg) {
        Snackbar.make(findViewById(R.id.activity_dic_edit), msg, Snackbar.LENGTH_SHORT).show();
    }

    private void bindListeners() {
        saveCodeBtn.setOnClickListener(v -> {
            // 保存按钮, 如果存在文件则保存后退出activity, 刷新dic, 否则弹窗输入文件名
            if (existFile) {
                try {
                    writeContentToFile(code.getText().toString(), filename);
                    toast("保存成功!");
                } catch (IOException e) {
                    toast("保存失败!");
                    throw new RuntimeException(e);
                }
            } else {
                askFilename(filename -> {
                    try {
                        createDIC(filename);
                    } catch (IOException e) {
                        toast("创建失败!");
                        throw new RuntimeException(e);
                    }
                });
            }
            System.out.println(code.getText());
        });
    }

    private void createDIC(String filename) throws IOException {
        filename = filename + GlobalConstant.dicFileExt;
        // 校验是否有同名文件
        boolean dicExist = this.getExternalFilesDir("dic").toPath().resolve(filename).toFile().exists();
        if (dicExist)
            toast("已存在同名文件!");
        else {
            writeContentToFile(code.getText().toString(), filename);
            toast("新建成功!");
            this.finish();
        }
    }

    private void writeContentToFile(String content, String filename) throws IOException {
        IOUtil.writeStringToFile(this.getExternalFilesDir("dic").toPath().resolve(filename).toFile().getAbsolutePath(), code.getText().toString());
    }
}
