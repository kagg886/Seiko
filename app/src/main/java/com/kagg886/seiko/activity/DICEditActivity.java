package com.kagg886.seiko.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.kagg886.seiko.R;
import com.kagg886.seiko.constant.GlobalConstant;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.util.IOUtil;
import io.github.rosemoe.sora.event.EditorKeyEvent;
import io.github.rosemoe.sora.event.EventReceiver;
import io.github.rosemoe.sora.event.Unsubscribe;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.analysis.StyleReceiver;
import io.github.rosemoe.sora.lang.completion.*;
import io.github.rosemoe.sora.lang.format.Formatter;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.lang.util.PlainTextAnalyzeManager;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.seikodictionaryenginev2.base.entity.DictionaryFile;
import io.github.seikodictionaryenginev2.base.entity.code.impl.FastAssignment;
import io.github.seikodictionaryenginev2.base.env.DICList;
import io.github.seikodictionaryenginev2.base.model.DICParseResult;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        code.setEditorLanguage(new SeikoDictionaryLanguage());
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
            filenameView.setText(new File(filename).getName());
            // 从文件读取出来
            Uri dicFileUri = Uri.fromFile(new File(filename));
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
                    IOUtil.writeStringToFile(filename, code.getText().toString());
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
            Optional<DICParseResult> r = DICList.INSTANCE.refresh().stream().filter((p) -> !p.success)
                    .filter((p) -> p.dicName.equals(new File(filename).getName())).findAny();
            r.ifPresent(dicParseResult -> DialogBroadCast.sendBroadCast(String.format(this.getString(R.string.dic_load_error), dicParseResult.dicName), IOUtil.getException(dicParseResult.err)));
        });

        ImageView undo = findViewById(R.id.undo);

        undo.setOnClickListener((v) -> {
            if (code.canUndo()) {
                code.undo();
            }
        });

        ImageView redo = findViewById(R.id.redo);

        redo.setOnClickListener((v) -> {
            if (code.canRedo()) {
                code.redo();
            }
        });
    }

    private void createDIC(String filename) throws IOException {
        filename = filename + GlobalConstant.dicFileExt;
        // 校验是否有同名文件
        boolean dicExist = this.getExternalFilesDir("dic").toPath().resolve(filename).toFile().exists();
        if (dicExist)
            toast(R.string.dic_edit_create_exist);
        else {
            IOUtil.writeStringToFile(filename, code.getText().toString());
            toast(R.string.dic_edit_create_success);
            this.finish();
        }
    }

    private class SeikoDictionaryLanguage implements Language {
        private final AnalyzeManager manager = new PlainTextAnalyzeManager();
        private final Formatter format = new EmptyLanguage.EmptyFormatter();

        @NonNull
        @NotNull
        @Override
        public AnalyzeManager getAnalyzeManager() {
            return manager;
        }

        @Override
        public int getInterruptionLevel() {
            return 0;
        }

        private final Function<String, Integer> depth = (v) -> {
            for (int i = 0; i < v.length(); i++) {
                if (v.charAt(i) == ' ') {
                    continue;
                }
                return i;
            }
            return 0;
        };

        @Override
        public void requireAutoComplete(@NonNull @NotNull ContentReference content, @NonNull @NotNull CharPosition position, @NonNull @NotNull CompletionPublisher publisher, @NonNull @NotNull Bundle extraArguments) throws CompletionCancelledException {
            String line = CompletionHelper.computePrefix(content, position, v -> true);

            int dep = depth.apply(line);
            if (line.contains("${") && line.indexOf("}", position.column - 1) < position.column) {
                int l = position.line;
                while (true) {
                    line = content.getLine(l--);
                    if (line.isEmpty()) {
                        break;
                    }
                    if (line.trim().contains("<-")) {
                        int dep1 = depth.apply(line);
                        int p = content.getLine(position.line).indexOf("}");
                        if (dep1 <= dep && !line.trim().startsWith("${") && (p == -1 || p >= position.column - 1)) {
                            String[] val = line.trim().split("<-");
                            publisher.addItem(new CompletionItem(val[0], val[1]) {
                                @Override
                                public void performCompletion(@NonNull @NotNull CodeEditor editor, @NonNull @NotNull Content text, int line1, int column) {
                                    String origin = content.getLine(line1);
                                    if (origin.charAt(Math.max(0, column - 1)) != '}') {
                                        int left = origin.indexOf("${");
                                        editor.getText().delete(line1, left + 2, line1, column);
                                        column = editor.getText().getLine(line1).length();
                                    }
                                    editor.getCursor().set(line1, column - 1);
                                    editor.commitText(val[0]);
                                    editor.getCursor().set(line1, column + val[0].length());
                                }
                            });
                        }
                    }
                }
                return;
            }

            if (line.trim().startsWith("$") && line.contains(" ")) {
                if (line.trim().equals("$$")) {
                    return;
                }
                String cmd = line.trim().substring(1);
                io.github.seikodictionaryenginev2.base.entity.code.func.Function.globalManager.entrySet()
                        .stream()
                        .filter((v) -> v.getKey().startsWith(cmd)).forEach((e) -> {
                            publisher.addItem(new CompletionItem(e.getKey(), "词库函数") {
                                @Override
                                public void performCompletion(@NonNull @NotNull CodeEditor editor, @NonNull @NotNull Content text, int line1, int column) {
                                    String origin = content.getLine(line1);
                                    editor.getText().delete(line1, origin.indexOf("$") + 1, line1, column);
                                    column = editor.getText().getLine(line1).length();
                                    editor.getCursor().set(line1, column - 1);
                                    editor.commitText(e.getKey() + " ");
                                    editor.getCursor().set(line1, column + e.getKey().length());
                                }
                            });
                        });
                return;
            }
        }

        @Override
        public int getIndentAdvance(@NonNull @NotNull ContentReference content, int line, int column) {
            String dic = content.getLine(line).trim();

            if (dic.startsWith("如果:") ||
                    dic.startsWith("试错:") ||
                    dic.startsWith("捕获") ||
                    dic.startsWith("循环:") ||
                    dic.startsWith("如果尾")
            ) {
                //如果尾补全
                if (dic.startsWith("如果尾")) {
                    String origin = content.getLine(line);
                    int depth = this.depth.apply(origin);
                    code.getText().delete(line, 0, line, origin.length());

                    StringBuilder b = new StringBuilder();
                    for (int i = 0; i < depth - 1; i++) {
                        b = b.append(" ");
                    }
                    code.commitText(b.append("如果尾"));
                    return depth == 0 ? 1 : 0;
                }
                return 1;
            }

            if (content.getLine(line).isEmpty()) {
                return 0;
            }

            if (dic.isEmpty()) {
                int space = 0;
                String origin = content.getLine(line);
                code.getText().delete(line, 0, line, origin.length());
                for (int i = 0; i < origin.length(); i++) {
                    if (origin.charAt(i) == ' ') {
                        space++;
                    }
                }
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < space - 1; i++) {
                    b = b.append(" ");
                }
                code.commitText(b);
                code.deleteText();
                return -1;
            }
            return 0;
        }

        @Override
        public boolean useTab() {
            return false;
        }

        @NonNull
        @NotNull
        @Override
        public Formatter getFormatter() {
            return format;
        }

        @Override
        public SymbolPairMatch getSymbolPairs() {
            SymbolPairMatch m = new SymbolPairMatch();
            m.putPair("${", new SymbolPairMatch.SymbolPair("{", "}"));
            m.putPair("$[", new SymbolPairMatch.SymbolPair("[", "]"));
            return m;
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public NewlineHandler[] getNewlineHandlers() {
            return new NewlineHandler[]{};
        }

        @Override
        public void destroy() {

        }
    }
}
