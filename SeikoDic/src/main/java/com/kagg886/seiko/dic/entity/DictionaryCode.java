package com.kagg886.seiko.dic.entity;

import androidx.annotation.NonNull;
import com.kagg886.seiko.dic.session.AbsRuntime;
import org.jetbrains.annotations.NotNull;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity
 * @className: DictionaryCommand
 * @author: kagg886
 * @description: 代表了一行伪代码
 * @date: 2023/1/11 15:25
 * @version: 1.0
 */
public class DictionaryCode {
    private final int line;
    private final String code;

    private final Type type;

    public DictionaryCode(int line, String code, Type type) {
        this.line = line;
        this.code = code;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    /*
     * @param :
     * @return String
     * @author kagg886
     * @description 清理变量
     * @date 2023/01/13 09:44
     */
    public String cleanVariableCode(AbsRuntime runtime) {
        //变量替换成常量
        String clone = code.replace("\\n", "\n");
        for (Object s : runtime.getRuntimeObject().keySet()) { //s一定是String
            String var = "%" + s.toString() + "%";
            if (clone.contains(var)) {
                Object q = runtime.getRuntimeObject().get(s);
                if (q == null) {
                    q = "null";
                }
                clone = clone.replace(var, q.toString());
            }
        }

        //计算表达式

        int x = 0;
        while ((x = clone.indexOf("[", x)) != -1) {
            int y = clone.indexOf("]", x);
            String expression = clone.substring(x + 1, y);
            String result = String.valueOf(runtime.mathExpressionCalc(expression));
            clone = clone.replace("[" + expression + "]", result);
            x = y;
        }

        return clone;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return line + ":" + code;
    }

    public enum Type {
        FUNCTION, PLAIN_TEXT
    }
}
