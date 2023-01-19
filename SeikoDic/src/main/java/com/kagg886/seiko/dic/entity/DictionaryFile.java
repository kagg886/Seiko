package com.kagg886.seiko.dic.entity;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.entity.impl.Expression;
import com.kagg886.seiko.dic.entity.impl.PlainText;
import com.kagg886.seiko.dic.exception.DictionaryOnLoadException;
import com.kagg886.seiko.util.ArrayIterator;
import com.kagg886.seiko.util.IOUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity
 * @className: Dictionary
 * @author: kagg886
 * @description: 代表了一个伪代码实例
 * @date: 2023/1/9 19:34
 * @version: 1.0
 */
public class DictionaryFile {
    private static final boolean STRICT_MODE = false;
    private final File dicFile;
    private final HashMap<DictionaryCommandMatcher, ArrayList<DictionaryCode>> commands = new HashMap<DictionaryCommandMatcher, ArrayList<DictionaryCode>>() {
        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public ArrayList<DictionaryCode> put(DictionaryCommandMatcher key, ArrayList<DictionaryCode> value) {
            if (STRICT_MODE) {
                for (DictionaryCommandMatcher d : keySet()) {
                    if (key.equals(d)) {
                        throw new DictionaryOnLoadException(dicFile.getName() + "含有冲突指令:" + key.getLine() + "-" + d.getLine());
                    }
                }
            }
            return super.put(key, value);
        }
    };

    public DictionaryFile(File dicFile) throws Throwable {
        this.dicFile = dicFile;
        String dicCodes = IOUtil.loadStringFromFile(dicFile.getAbsolutePath());
        if (dicCodes.length() == 0) {
            throw new DictionaryOnLoadException("[" + dicFile.getName() + "]为空!");
        }
        String[] lines = dicCodes.split("\n");
        int start = 0;
        for (int i = 0; i < lines.length; i++) {
            if (!TextUtils.isEmpty(lines[i])) {
                start = i;
                break;
            }
        }
        ArrayIterator<String> iterator = new ArrayIterator<>(lines);
        iterator.setLen(start);

        boolean behindLineIsEmpty = true;
        String commandRegex = null;
        ArrayList<DictionaryCode> dictionaryCodes = new ArrayList<>();
        int commandLine = 0; //指令所在的行号
        while (iterator.hasNext()) {
            String comm = iterator.next();
            if (comm.startsWith("//")) { //注释判空处理
                comm = "";
            }
            if (behindLineIsEmpty) {
                if (TextUtils.isEmpty(comm)) {
                    continue;
                }
                commandRegex = comm;
                behindLineIsEmpty = false;
                commandLine = iterator.getLen();
                continue;
            }
            if (TextUtils.isEmpty(comm)) {
                if (dictionaryCodes.size() == 0) {
                    throw new DictionaryOnLoadException("指令无伪代码实现:" + commandRegex + "(" + dicFile.getName() + ":" + (iterator.getLen() - 1) + ")");
                }
                commands.put(new DictionaryCommandMatcher(commandRegex, commandLine, dicFile), dictionaryCodes);
                dictionaryCodes = new ArrayList<>();
                behindLineIsEmpty = true;
                continue;
            }
            if (comm.startsWith("$")) {
                try {
                    dictionaryCodes.add(Function.parseFunction(comm, iterator.getLen()));
                } catch (Throwable e) {
                    throw new DictionaryOnLoadException("解析词库方法时出错!" + "(" + iterator.getLen() + ":" + comm + ")", e);
                }
            } else if (comm.startsWith("如果:")) {
                dictionaryCodes.add(new Expression.If(iterator.getLen(), comm));
            } else if (comm.equals("如果尾")) {
                dictionaryCodes.add(new Expression.Else(iterator.getLen(), comm));
            } else if (comm.equals("返回")) {
                dictionaryCodes.add(new Expression.Return(iterator.getLen(), comm));
            } else {
                dictionaryCodes.add(new PlainText(iterator.getLen(), comm));
            }
        }
        if (iterator.getLen() == lines.length) {
            commands.put(new DictionaryCommandMatcher(commandRegex, commandLine, dicFile), dictionaryCodes);
        }
    }

    public File getFile() {
        return dicFile;
    }

    public HashMap<DictionaryCommandMatcher, ArrayList<DictionaryCode>> getCommands() {
        return commands;
    }

    public String getName() {
        return getFile().getName();
    }
}
