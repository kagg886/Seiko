package com.kagg886.seiko.dic.entity.func;

import com.kagg886.seiko.dic.DictionaryUtil;
import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.impl
 * @className: Function
 * @author: kagg886
 * @description: 代表一个函数，最好不要直接继承它。
 * @date: 2023/1/16 17:25
 * @version: 1.0
 */
public abstract class Function extends DictionaryCode {
    private static final String[][] interruptedFunctionNames = { //阻断方法列表，后面的是Class名
            {"延时", "Delay"},
            {"语音", "Ptt"},
            {"访问", "HTTP"}
    };
    private static final String[][] uninterruptedFunctionNames = { //阻断方法列表，后面的是Class名
            {"图片", "addImage"},
            {"读", "FileControl$Read"},
            {"写", "FileControl$Write"},
            {"JAVA方法运行", "JavaMethodInvoke"},
            {"随机数", "Random"},
            {"调用", "TransferDicMethod"},

            {"群成员列表","MemberControl$MemberList"},
            {"改名","MemberControl$ChangeName"},
            {"群头衔","MemberControl$ChangeTitle"},
            {"禁言","MemberControl$Mute"},
            {"踢","MemberControl$Kick"},
            {"管理员","MemberControl$ModifyAdmin"},

            {"转时间","DateParser$StampToDate"},
            {"转时间戳","DateParser$DateToStamp"},

            {"文字替换", "StringUtil$Replace"},
            {"文字分割", "StringUtil$Split"},
            {"文字查索", "StringUtil$IndexOf"},
            {"文字匹配", "StringUtil$Matches"},
            {"文字包含", "StringUtil$Contains"},
            {"转大写", "StringUtil$UpperCase"},
            {"转小写", "StringUtil$LowerCase"},

            {"变量赋值", "ContextControl$SetVar"},
            {"变量删除", "ContextControl$DelVar"},
            {"变量检验", "ContextControl$CheckExists"},
            {"变量提取", "ContextControl$ChainGet"},

            {"集合创建", "CollectionControl$Create"},
            {"集合导入", "CollectionControl$From"},
            {"集合删除", "CollectionControl$DelVar"},
            {"集合赋值", "CollectionControl$SetVar"},
            {"取集合", "CollectionControl$Get"},
            {"集合检验", "CollectionControl$CheckExists"},
            {"集合长", "CollectionControl$Length"},
            {"集合转", "CollectionControl$To"},

            {"数组创建", "ArrayControl$Create"},
            {"数组导入", "ArrayControl$Import"},
            {"数组删除", "ArrayControl$Del"},
            {"数组赋值", "ArrayControl$Put"},
            {"取数组", "ArrayControl$Get"},
            {"数组长", "ArrayControl$Length"},
            {"数组转", "ArrayControl$To"},
    };

    private final String argCode; //去除包装后剩下的参数字符串

    public Function(int line, String code) {
        super(line, code);
        int sIndex = code.indexOf(" ");
        argCode = code.substring(sIndex + 1, code.length() - 1);
    }

    /*
     * @param dicLine:
     * @param line:
     * @return Function
     * @author kagg886
     * @description 返回一个伪代码函数对象。使用反射实现
     * @date 2023/01/28 21:37
     */
    public static Function parseFunction(String dicLine, int line) throws Throwable { //一定是$xxxx a b c$
        for (String[] p : interruptedFunctionNames) {
            int spaceIndex = dicLine.indexOf(" ");
            if (spaceIndex == -1) {
                spaceIndex = dicLine.length();
            }
            String command = dicLine.substring(1, spaceIndex);
            if (command.equals(p[0])) {
                Class<?> func = Class.forName("com.kagg886.seiko.dic.entity.func.interrupted." + p[1]);
                return (Function) func.getConstructor(int.class, String.class).newInstance(line, dicLine);
            }
        }

        for (String[] p : uninterruptedFunctionNames) {
            int spaceIndex = dicLine.indexOf(" ");
            if (spaceIndex == -1) {
                spaceIndex = dicLine.length();
            }
            String command = dicLine.substring(1, spaceIndex);
            if (command.equals(p[0])) {
                Class<?> func = Class.forName("com.kagg886.seiko.dic.entity.func.uninterrupted." + p[1]);
                return (Function) func.getConstructor(int.class, String.class).newInstance(line, dicLine);
            }
        }
        throw new NoSuchFieldException("未找到伪代码方法");
    }

    public void invoke(AbsRuntime<?> runtime) {
        run(runtime, DictionaryUtil.variableToObject(argCode, runtime));
    }

    protected abstract void run(AbsRuntime<?> runtime, List<Object> args);

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.impl
     * @className: InterruptedFunction
     * @author: kagg886
     * @description: 代表一个缓冲区阻断函数。在调用此函数前，需要清空并发送缓冲区内内容。
     * @date: 2023/1/18 11:43
     * @version: 1.0
     */
    public static abstract class InterruptedFunction extends Function {

        public InterruptedFunction(int line, String code) {
            super(line, code);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.impl
     * @className: InterruptedFunction
     * @author: kagg886
     * @description: 代表一个缓冲区非阻断函数。在调用此函数前，不需要清空并发送缓冲区内内容。
     * @date: 2023/1/18 11:45
     * @version: 1.0
     */
    public static abstract class UnInterruptedFunction extends Function {

        public UnInterruptedFunction(int line, String code) {
            super(line, code);
        }
    }
}
