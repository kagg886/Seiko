package com.kagg886.seiko.dic.entity.func;

import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.util.DictionaryUtil;

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
    private static final String[][] dicFunctions = { //阻断方法列表，后面的是Class名
            //杂项方法
            {"JAVA方法运行", "JavaMethodInvoke"},
            {"随机数", "Random"},
            {"调用", "TransferDicMethod"},
            {"异步调用", "TransferDicMethod$Async_Call"},
            {"延时", "Delay"},
            {"访问", "HTTP"},
            {"日志","DebugTool$Logcat"},
            {"设置捕错函数","DebugTool$CatchFunction"},



            {"装载", "ModuleUtil$Add"},
            {"卸载", "ModuleUtil$Delete"},
            {"子文件调用", "ModuleUtil$Caller"},

            //与消息有关
            {"图片", "MessageControl$addImage"},
            {"艾特", "MessageControl$addAt"},
            {"撤回","MessageControl$Recall"},
            {"设置回复","MessageControl$Reply"},
            {"设置接收者", "MessageControl$setSender"},
            {"语音", "Ptt"},

            //与文件有关
            {"读", "FileControl$Read"},
            {"写", "FileControl$Write"},
            {"删","FileControl$DelFile"},
            {"读文件","FileControl$ReadFile"},
            {"写文件","FileControl$WriteFile"},

            //与群成员有关
            {"群成员", "MemberControl$GetMember"},
            {"群成员列表", "MemberControl$MemberList"},
            {"改名", "MemberControl$ChangeName"},
            {"群头衔", "MemberControl$ChangeTitle"},
            {"禁言", "MemberControl$Mute"},
            {"踢", "MemberControl$Kick"},
            {"管理员", "MemberControl$ModifyAdmin"},

            //与群对象有关
            {"退群","GroupControl$Exit"},
            {"获取群公告","GroupControl$GetGroupAnnouncement"},
            {"删除群公告","GroupControl$DelGroupAnnouncement"},
            {"发布群公告","GroupControl$PublishGroupAnnouncement"},
            {"进群申请处理","GroupControl$DealMemberJoin"},
//            {"","GroupControl$"},
//            {"","GroupControl$"},
//            {"","GroupControl$"},
//            {"","GroupControl$"},
//            {"","GroupControl$"},

            //与群文件有关
            {"群文件", "GroupFileControl$GetFile"},

            //与bot有关
            {"BOT列表","BotControl$getBot"},
            {"群列表", "BotControl$getGroups"},
            {"获取群", "BotControl$getGroup"},
            {"好友列表", "BotControl$getFriends"},
            {"获取好友", "BotControl$getFriend"},

            //与日期有关
            {"转时间", "DateParser$StampToDate"},
            {"时间格式化", "DateParser$StampToDateNew"},
            {"设置时间格式","DateParser$StampSet"},
            {"转时间戳", "DateParser$DateToStamp"},

            //与字符串有关
            {"文字替换", "StringUtil$Replace"},
            {"文字分割", "StringUtil$Split"},
            {"文字查索", "StringUtil$IndexOf"},
            {"文字匹配", "StringUtil$Matches"},
            {"文字包含", "StringUtil$Contains"},
            {"转大写", "StringUtil$UpperCase"},
            {"转小写", "StringUtil$LowerCase"},
            {"格式化", "StringUtil$Format"},

            //与变量，常量有关
            {"变量赋值", "ContextControl$SetVar"},
            {"变量删除", "ContextControl$DelVar"},
            {"变量检验", "ContextControl$CheckExists"},
            {"变量提取", "ContextControl$ChainGet"},
            {"取全局变量", "ContextControl$Constant"},
            {"置全局变量", "ContextControl$SetConstant"},

            //与集合有关
            {"集合创建", "CollectionControl$Create"},
            {"集合导入", "CollectionControl$From"},
            {"集合删除", "CollectionControl$DelVar"},
            {"集合赋值", "CollectionControl$SetVar"},
            {"取集合", "CollectionControl$Get"},
            {"集合检验", "CollectionControl$CheckExists"},
            {"集合长", "CollectionControl$Length"},
            {"集合转", "CollectionControl$To"},

            //与数组有关
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

        for (String[] p : dicFunctions) {
            int spaceIndex = dicLine.indexOf(" ");
            if (spaceIndex == -1) {
                spaceIndex = dicLine.length();
            }
            String command = dicLine.substring(1, spaceIndex);
            if (command.equals(p[0])) {
                Class<?> func = Class.forName("com.kagg886.seiko.dic.entity.func.impl." + p[1]);
                return (Function) func.getConstructor(int.class, String.class).newInstance(line, dicLine);
            }
        }
        throw new NoSuchFieldException("未找到伪代码方法");
    }

    public void invoke(AbsRuntime<?> runtime) {
        int limit = 0;
        if (this instanceof ArgumentLimiter) {
            limit = ((ArgumentLimiter) this).getArgumentLength();
        }
        invoke(runtime, DictionaryUtil.variableToObject(argCode.split(" ", limit), runtime));
    }

    public void invoke(AbsRuntime<?> runtime,List<Object> args) {
        run(runtime,args);
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

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.impl
     * @className: ArgumentLimiter
     * @author: kagg886
     * @description: 限制了参数的Function，最多只能拥有getArgumentLength()个参数
     * @date: 2023/3/28 12:42
     * @version: 1.0
     */
    public interface ArgumentLimiter {
        int getArgumentLength();
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.impl
     * @className: Deprecated
     * @author: kagg886
     * @description: 实现此接口的方法将不再维护。此外，伪代码被加载时若使用了这些方法则会输出警告提示。
     * @date: 2023/3/30 13:08
     * @version: 1.0
     */
    public interface Deprecated {
        String getAdvice();
    }
}
