package com.kagg886.seiko.dic.entity;

import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.entity.impl.Expression;
import com.kagg886.seiko.dic.entity.impl.PlainText;
import com.kagg886.seiko.dic.exception.DictionaryOnLoadException;
import com.kagg886.seiko.dic.session.impl.LifeCycleRuntime;
import com.kagg886.seiko.util.ArrayIterator;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    private final HashMap<DictionaryCommandMatcher, ArrayList<DictionaryCode>> commands = new HashMap<>() {
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

    private final LifeCycleRuntime cycle;
    private final HashMap<String,Object> settings = new HashMap<>(); //伪代码的#号设置

    private final HashMap<String,DictionaryFile> subFile = new HashMap<>();


    private ThreadPoolExecutor executor;

    private final char[] illegalChar = {
            '％',
            160,
            12288
    };

    public DictionaryFile(File dicFile) {
        this.dicFile = dicFile;
        if (!dicFile.exists() || dicFile.isDirectory()) {
            throw new DictionaryOnLoadException(dicFile.getName() + "不存在");
        }
        cycle = new LifeCycleRuntime(this);
    }

    // 清除变量
    private void clear() {
        if (commands.size() != 0) {
            notifyLifeCycle(LifeCycleRuntime.LifeCycle.DESTROY);
        }
        commands.clear();
        settings.clear();
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public HashMap<String, Object> getSettings() {
        return settings;
    }

    public void parseDICCodeFile() throws IOException {
        // 先clear
        clear();

        String dicCodes = IOUtil.loadStringFromFile(dicFile.getAbsolutePath()).replace("\r", "");
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
        boolean initConfig = false, //遇到了'#'开头的内容返回true
                initConfigSuccess = false; //在遇到'#'后，若遇到了空行返回true

        int headerLine = 0; //解析伪代码头部时存入的变量键
        while (iterator.hasNext()) {
            String comm = iterator.next();
            if (comm.startsWith("//")) { //注释判空处理
                continue; //注释直接跳过
            }

            if (!initConfig || !initConfigSuccess) { //跳过解析#的条件:在遇到#后遇到空行
                if (comm.startsWith("#")) {
                    initConfig = true;
                    settings.put(String.valueOf(headerLine++),comm.substring(1));
                    continue;
                }
                if (TextUtils.isEmpty(comm)) {
                    initConfigSuccess = true;
                    //在此处判断这是不是能正确解析SeikoDIC，先从编码开始
                    if (!settings.containsValue("Seiko词库")) {
                        throw new DictionaryOnLoadException("未检测到必要的标识:Seiko词库。如果你的编辑器里有'#Seiko词库'，那可能说明词库的编码出了问题，请将其以别的编码保存直到此报错消失\n出错的伪代码文件:" + this.dicFile.getAbsolutePath());
                    }
                    continue;
                }
                throw new DictionaryOnLoadException("请在伪代码文件开头通过#注册必要设置!\n出错的伪代码文件:" + this.dicFile.getAbsolutePath());
            }

            for (char a : illegalChar) {
                int idx;
                if ((idx = comm.indexOf(a)) != -1) {
                    DictionaryEnvironment.getInstance().getErrorListener().onWarn(dicFile, "第" + iterator.getLen() + "行的第" + (idx+1) + "个字符是非ASCII字符。\n在某些设备上人的肉眼无法准确辨别，可能会导致伪代码变量无法正确解析");
                }
            }
//            if (comm.contains("％")) { //我帮你排错... 我居然分不清这两个符号。2023/2/18
//                DictionaryEnvironment.getInstance().getErrorListener().onWarn(dicFile, "在第" + iterator.getLen() + "行发现全角符号％。\n在某些设备上%和％无法准确辨别，可能会导致伪代码变量无法正确解析");
//            }
            if (behindLineIsEmpty) {
                /*
                 判断此行的上一行是否为空。
                 若为空则判断此行是否为空，
                 不为空证明此行是指令开始解析指令。
                 */
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
                    //排除只有指令没有伪代码实现的情况
                    throw new DictionaryOnLoadException("指令无伪代码实现:" + commandRegex + "(" + dicFile.getName() + ":" + (iterator.getLen() - 1) + ")");
                }
                /*
                    证明这一行指令领导的伪代码已经解析完了，
                    下面的代码用于装载解析完毕的伪代码示例
                 */
                commands.put(new DictionaryCommandMatcher(commandRegex, commandLine, dicFile), dictionaryCodes);
                dictionaryCodes = new ArrayList<>();
                behindLineIsEmpty = true;
                continue;
            }
            /*
                对每一行伪代码进行解析。
                按照[函数->特殊控制字符->纯文本]解析
            */
            if (comm.startsWith("$") && comm.endsWith("$")) { //真的会有人最后一行跟换行符(
                try {
                    Function func = Function.parseFunction(comm, iterator.getLen());
                    if (func instanceof Function.Deprecated) {
                        DictionaryEnvironment.getInstance().getErrorListener().onWarn(this.getFile(),"发现过时函数:" + func.getCode() + "\n" + ((Function.Deprecated) func).getAdvice());
                    }
                    dictionaryCodes.add(func);
                } catch (Throwable e) {
                    throw new DictionaryOnLoadException("解析伪代码方法时出错!" + "(" + iterator.getLen() + ":" + comm + ")", e);
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
        /*
            最后一行若不是空的话，需要强行装载一下
         */

        if (iterator.getLen() == lines.length) {
            commands.put(new DictionaryCommandMatcher(commandRegex, commandLine, dicFile), dictionaryCodes);
        }

        notifyLifeCycle(LifeCycleRuntime.LifeCycle.INIT);
    }

    public void notifyLifeCycle(LifeCycleRuntime.LifeCycle str) {
        cycle.invoke(str.getTips());

        if (str == LifeCycleRuntime.LifeCycle.INIT) {
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }

        if (str == LifeCycleRuntime.LifeCycle.DESTROY) {
            executor.shutdown();
            executor = null;
        }
    }

    public File getFile() {
        return dicFile;
    }

    public HashMap<String, DictionaryFile> getSubFile() {
        return subFile;
    }

    public HashMap<DictionaryCommandMatcher, ArrayList<DictionaryCode>> getCommands() {
        return commands;
    }

    public String getName() {
        return getFile().getName();
    }
}
