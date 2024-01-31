package com.kagg886.seiko.activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.kagg886.seiko.R;
import com.kagg886.seiko.constant.GlobalConstant;
import com.kagg886.seiko.util.IOUtil;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.widget.CodeEditor;

import java.io.IOException;
import java.util.function.Consumer;

public class DICEditActivity extends AppCompatActivity {
    private CodeEditor code;
    private Button saveCodeBtn;
    private TextView filenameView;

    private Boolean existFile = false;

    private String filename = null;

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

        registerTemplate("${}", v -> {
            code.commitText("${}");
        });
        registerTemplate("$[]", v -> {
            code.commitText("$[]");
        });
        registerTemplate("$$", v -> {
            code.commitText("$$");
        });

        bindListeners();
    }

    private void registerTemplate(String text, View.OnClickListener l) {
        LinearLayout lay = findViewById(R.id.templates);
        TextView v = LayoutInflater.from(this).inflate(R.layout.template_dic_edit, null).findViewById(R.id.text);
        v.setText(text);
        v.setOnClickListener(l);

        lay.addView(v);
    }

    private void init() throws IOException {
        saveCodeBtn.setActivated(false);
        existFile = getIntent().getBooleanExtra("exist_file", false);
        if (existFile) {
            saveCodeBtn.setText(R.string.dic_edit_save_edit);
        } else {
            code.setText(IOUtil.loadStringFromStream(getAssets().open("dic_template.txt")));
            saveCodeBtn.setText(R.string.dic_edit_save_create);
        }
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
        builder.setTitle(R.string.dic_edit_input_name);
        View v = LayoutInflater.from(this).inflate(R.layout.ask_dic_name, null);
        EditText edt = v.findViewById(R.id.dialog_dicName);
        builder.setView(v);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String name = edt.getText().toString();
            if (TextUtils.isEmpty(name)) {
                toast(R.string.dic_edit_input_not_empty);
            } else {
                stringConsumer.accept(name);
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        builder.show();
    }

    private void toast(@StringRes int msg) {
        Snackbar.make(findViewById(R.id.activity_dic_edit), msg, Snackbar.LENGTH_SHORT).show();
    }

    private void bindListeners() {
        saveCodeBtn.setOnClickListener(v -> {
            // 保存按钮, 如果存在文件则保存后退出activity, 刷新dic, 否则弹窗输入文件名
            if (existFile) {
                try {
                    writeContentToFile(code.getText().toString(), filename);
                    toast(R.string.dic_edit_save_success);
                } catch (IOException e) {
                    toast(R.string.dic_edit_save_fail);
                    throw new RuntimeException(e);
                }
            } else {
                askFilename(filename -> {
                    try {
                        createDIC(filename);
                    } catch (IOException e) {
                        toast(R.string.dic_edit_create_fail);
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
            toast(R.string.dic_edit_create_exist);
        else {
            writeContentToFile(code.getText().toString(), filename);
            toast(R.string.dic_edit_create_success);
            this.finish();
        }
    }

    private void writeContentToFile(String content, String filename) throws IOException {
        IOUtil.writeStringToFile(this.getExternalFilesDir("dic").toPath().resolve(filename).toFile().getAbsolutePath(), code.getText().toString());
    }
}
