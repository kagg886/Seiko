package com.kagg886.seiko.dic.entity.func.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: StringUtil
 * @author: kagg886
 * @description: 操作字符串的函数
 * @date: 2023/2/18 18:02
 * @version: 1.0
 */
public abstract class StringUtil extends Function.UnInterruptedFunction {

    public StringUtil(int line, String code) {
        super(line, code);
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: StringUtil
     * @author: kagg886
     * @description: $格式化 存入变量 格式化字符串 参数$
     * @date: 2023/2/18 18:48
     * @version: 1.0
     */
    public static class Format extends CollectionControl {
        public Format(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVal = args.get(0).toString();
            String format = args.get(1).toString();
            Object[] formatArg = new Object[args.size() - 2];
            for (int i = 2; i < args.size(); i++) {
                formatArg[i-2] = args.get(i);
            }
            runtime.getRuntimeObject().put(putVal,String.format(format,formatArg));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: StringUtil
     * @author: kagg886
     * @description: $转小写 变量名 存入变量(可选)$
     * @date: 2023/2/18 18:48
     * @version: 1.0
     */
    public static class LowerCase extends CollectionControl {
        public LowerCase(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVarName = args.get(0).toString();
            String dx = runtime.getRuntimeObject().get(putVarName).toString();
            if (args.size() == 2) {
                putVarName = args.get(1).toString();
            }
            runtime.getRuntimeObject().put(putVarName, dx.toLowerCase(Locale.ROOT));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: StringUtil
     * @author: kagg886
     * @description: $转大写 变量名 存入变量(可选)$
     * @date: 2023/2/18 18:48
     * @version: 1.0
     */
    public static class UpperCase extends CollectionControl {
        public UpperCase(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVarName = args.get(0).toString();
            String dx = runtime.getRuntimeObject().get(putVarName).toString();
            if (args.size() == 2) {
                putVarName = args.get(1).toString();
            }
            runtime.getRuntimeObject().put(putVarName, dx.toUpperCase(Locale.ROOT));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: StringUtil
     * @author: kagg886
     * @description: $文字包含 待匹配变量名 文字 存入变量(可选)$
     * @date: 2023/2/18 18:44
     * @version: 1.0
     */
    public static class Contains extends CollectionControl {

        public Contains(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVarName = args.get(0).toString();
            String dx = runtime.getRuntimeObject().get(putVarName).toString();
            String regex = args.get(1).toString();
            if (args.size() == 3) {
                putVarName = args.get(2).toString();
            }
            runtime.getRuntimeObject().put(putVarName, String.valueOf(dx.contains(regex))); //和其他伪代码方法兼容
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: StringUtil
     * @author: kagg886
     * @description: $文字匹配 待匹配变量名 要匹配的正则表达式 存入变量(可选)$
     * @date: 2023/2/18 18:42
     * @version: 1.0
     */
    public static class Matches extends CollectionControl {

        public Matches(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVarName = args.get(0).toString();
            String dx = runtime.getRuntimeObject().get(putVarName).toString();
            String regex = args.get(1).toString();
            if (args.size() == 3) {
                putVarName = args.get(2).toString();
            }
            runtime.getRuntimeObject().put(putVarName, String.valueOf(dx.matches(regex))); //和其他伪代码方法兼容
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: StringUtil
     * @author: kagg886
     * @description: $文字查索 待查询变量名 查询的文字 存入变量(可选)$
     * @date: 2023/2/18 18:34
     * @version: 1.0
     */
    public static class IndexOf extends CollectionControl {

        public IndexOf(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVarName = args.get(0).toString();
            String dx = runtime.getRuntimeObject().get(putVarName).toString();
            String index = args.get(1).toString();
            if (args.size() == 3) {
                putVarName = args.get(2).toString();
            }
            runtime.getRuntimeObject().put(putVarName, String.valueOf(dx.indexOf(index))); //和其他伪代码方法兼容
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: StringUtil
     * @author: kagg886
     * @description: $文字分割 待分割变量名 分割符号 存入变量(可选)$
     * @date: 2023/2/18 18:23
     * @version: 1.0
     */
    public static class Split extends CollectionControl {

        public Split(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVarName = args.get(0).toString();
            String dx = runtime.getRuntimeObject().get(putVarName).toString();
            String split = args.get(1).toString();
            if (args.size() == 3) {
                putVarName = args.get(2).toString();
            }

            runtime.getRuntimeObject().put(putVarName, new ArrayList<>(Stream.of(dx.split(split)).collect(Collectors.toList())));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: StringUtil
     * @author: kagg886
     * @description: $文字替换 待替换变量名 被替换文字 替换成... 存入变量(可选)$
     * @date: 2023/2/18 18:17
     * @version: 1.0
     */
    public static class Replace extends CollectionControl {

        public Replace(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVarName = args.get(0).toString();
            String dx = runtime.getRuntimeObject().get(putVarName).toString();
            String from = args.get(1).toString();
            String to = args.get(2).toString();
            if (args.size() == 4) {
                putVarName = args.get(3).toString();
            }
            runtime.getRuntimeObject().put(putVarName, dx.replace(from, to));
        }


    }
}
