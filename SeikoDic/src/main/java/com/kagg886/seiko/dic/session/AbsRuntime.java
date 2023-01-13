package com.kagg886.seiko.dic.session;

import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.DictionaryCommandMatcher;
import com.kagg886.seiko.dic.entity.DictionaryFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session
 * @className: AbsRuntime
 * @author: kagg886
 * @description: 代表一次处理。插件收到事件后，会匹配符合正则表达式的条目
 * @date: 2023/1/12 21:21
 * @version: 1.0
 */
public abstract class AbsRuntime<T> {
    protected final T event; //此次执行伪代码所需要的事件
    protected DictionaryFile file; //被执行的伪代码指令集
    protected HashMap<String, Object> context; //此次伪代码执行过程中存取的变量

    public AbsRuntime(DictionaryFile file, T event) {
        this.file = file;
        this.event = event;
        context = new HashMap<>();
    }

    public HashMap<String, Object> getRuntimeObject() {
        return context;
    }

    public void invoke(String command) {
        for (Map.Entry<DictionaryCommandMatcher, ArrayList<DictionaryCode>> entry : file.getCommands().entrySet()) {
            DictionaryCommandMatcher matcher = entry.getKey();
            ArrayList<DictionaryCode> code = entry.getValue();
            if (!matcher.matchesDomain(event)) {
                return;
            }
            if (matcher.matchesCommand(command)) {
                invoke(code);
            }
        }
    }

    protected abstract void invoke(ArrayList<DictionaryCode> code);


    public Double mathExpressionCalc(String str) {
        Double a = null;
        try {
            a = Double.parseDouble(str);
        } catch (NumberFormatException e) {
        }

        if (str.isEmpty() || a != null) {
            return str.isEmpty() ? 0 : a;
        }

        if (str.contains(")")) {
            // 最后一个左括号
            int lIndex = str.lastIndexOf("(");
            // 对于的右括号
            int rIndex = str.indexOf(")", lIndex);
            return mathExpressionCalc(str.substring(0, lIndex) + mathExpressionCalc(str.substring(lIndex + 1, rIndex)) + str.substring(rIndex + 1));
        }
        if (str.contains("+")) {
            int index = str.lastIndexOf("+");
            return mathExpressionCalc(str.substring(0, index)) + mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("-")) {
            int index = str.lastIndexOf("-");
            return mathExpressionCalc(str.substring(0, index)) - mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("*")) {
            int index = str.lastIndexOf("*");
            return mathExpressionCalc(str.substring(0, index)) * mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("/")) {
            int index = str.lastIndexOf("/");
            return mathExpressionCalc(str.substring(0, index)) / mathExpressionCalc(str.substring(index + 1));
        }

        if (str.contains("^")) {
            int index = str.lastIndexOf("^");
            return Math.pow(mathExpressionCalc(str.substring(0, index)), mathExpressionCalc(str.substring(index + 1)));
        }

        if (str.contains("%")) {
            int index = str.lastIndexOf("%");
            return mathExpressionCalc(str.substring(0, index)) % mathExpressionCalc(str.substring(index + 1));
        }
        // 出错
        throw new RuntimeException("无法解析的表达式:" + str);
    }
}
