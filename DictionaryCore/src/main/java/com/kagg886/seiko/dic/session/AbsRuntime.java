package com.kagg886.seiko.dic.session;

import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.DictionaryCommandMatcher;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.entity.impl.Expression;
import com.kagg886.seiko.dic.entity.impl.FastAssignment;
import com.kagg886.seiko.dic.entity.impl.PlainText;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.impl.FunctionRuntime;
import com.kagg886.seiko.dic.util.DictionaryUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.regex.Matcher;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session
 * @className: AbsRuntime
 * @author: kagg886
 * @description: 代表一次运行时。插件收到事件后，会匹配符合正则表达式的条目，然后构造运行时进行处理
 * @date: 2023/1/12 21:21
 * @version: 1.0
 */
public abstract class AbsRuntime<EVENT> {
    protected final EVENT event; //此次执行伪代码所需要的事件
    protected Contact contact; //联系人对象，暴露出来是为了往其他群主动发消息用
    protected DictionaryFile file; //被执行的伪代码指令集
    protected HashMap<String, Object> context; //此次伪代码执行过程中存取的变量
    protected Stack<String> exceptionStacks; //词库调用栈，每次*执行完一条命令*就会存储一条信息到栈中。

    protected Supplier<FunctionRuntime> exceptionCaller; //异常捕捉器

    protected String exceptionCaller_name; //异常捕捉器调用的方法


    /*
     * @param file: 需要执行的dicFile
     * @param event: 此次执行伪代码所需要的事件
     * @author kagg886
     * @description 构造函数
     * @date 2023/01/19 19:53
     */
    public AbsRuntime(DictionaryFile file, EVENT event) {
        this.file = file;
        this.event = event;
        context = new HashMap<>();
        exceptionStacks = new Stack<>();
        contact = initContact(event);

        //通用的变量会存储在这里。
        context.put("上下文", event);
        context.put("缓冲区", new MessageChainBuilder());
        context.put("时间戳", System.currentTimeMillis());
    }

    protected abstract Contact initContact(EVENT EVENT); //初始化联系人对象

    public Stack<String> getExceptionStacks() {
        return exceptionStacks;
    }

    public DictionaryFile getFile() {
        return file;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public MessageChainBuilder getMessageCache() {
        return (MessageChainBuilder) context.get("缓冲区");
    }

    public void clearMessage() { //清空缓冲区，之所以如此设计是因为不同事件发送消息的方法是不同的
        if (getMessageCache().size() == 0) {
            return;
        }
        contact.sendMessage(getMessageCache().build());
        context.put("缓冲区", new MessageChainBuilder());
    }

    public void setExceptionCaller(Supplier<FunctionRuntime> exceptionCaller,String name) {
        this.exceptionCaller = exceptionCaller;
        this.exceptionCaller_name = name;
    }

    public String getExceptionCaller_name() {
        return exceptionCaller_name;
    }

    public Supplier<FunctionRuntime> getExceptionCaller() {
        return exceptionCaller;
    }

    public EVENT getEvent() {
        return event;
    }

    public HashMap<String, Object> getRuntimeObject() {
        return context;
    }

    public Class<?> getProxyClass() {
        return event.getClass();
    }

    //全局invoke，用以捕捉异常。
    public void invoke(String command) {
        try {
            invoke0(command);
        } catch (Throwable e) {
            if (exceptionCaller == null) {
                throw e;
            }
            FunctionRuntime runtime = exceptionCaller.get();

            if (e instanceof DictionaryOnRunningException) {
                runtime.getRuntimeObject().put("错误信息",((DictionaryOnRunningException) e).getMsg());
            } else {
                runtime.getRuntimeObject().put("错误信息",e.getMessage());
            }
            runtime.getRuntimeObject().put("错误名",e.getClass().getName());

            runtime.invoke(exceptionCaller_name);
        }
    }


    /*
     * @param command: 指令
     * @return void
     * @author kagg886
     * @description 这个invoke用于匹配合适的伪代码并执行
     * @date 2023/01/19 19:54
     */
    private void invoke0(String command) {
        for (Map.Entry<DictionaryCommandMatcher, List<DictionaryCode>> entry : file.getCommands().entrySet()) {
            DictionaryCommandMatcher matcher = entry.getKey();
            List<DictionaryCode> code = entry.getValue();
            if (!matcher.matchesDomain(this)) { //匹配指令触发的环境和当前环境是否相符
                continue;
            }
            if (matcher.matchesCommand(command)) { //正则匹配
                String[] x = command.split(" ");
                if (!context.containsKey("文本")) {
                    context.put("文本", command);
                }
                context.put("参数长", x.length);
                if (x.length != 1) {
                    for (int i = 0; i < x.length; i++) {
                        context.put("参数" + i, x[i]);
                    }
                }
                Matcher groupFinder = matcher.getPattern().matcher(command);
                int groups = 0;
                while (groupFinder.find()) {
                    int i;
                    if (groups == 0) {
                        for (i = 1; i <= groupFinder.groupCount(); i++) {
                            context.put("括号" + i, groupFinder.group(i));
                        }
                        context.put("括号组", --i);
                    } else {
                        //TODO 可能永远也不会被调用，待测试
                        for (i = 1; i <= groupFinder.groupCount(); i++) {
                            context.put("括号" + groups + "_" + i, groupFinder.group(i));
                        }
                        context.put("括号组" + groups, --i);
                    }
                    groups++;
                }
                try {
                    invoke(code,true);
                } catch (Exception e) { //异常处理，生成调用栈信息向上抛出
                    String msg = e.getMessage();
                    if (e instanceof DictionaryOnRunningException) {
                        msg = ((DictionaryOnRunningException) e).getMsg();
                    }
                    throw new DictionaryOnRunningException(file, msg, this ,exceptionStacks, e);
                    //我也不知道这一坨怎么写的，能正常运行就行
                }
            }
        }
    }

    /*
     * @param code: 伪代码集
     * @return void
     * @author kagg886
     * @description 内部invoke函数。将在这里完成对dic最终的解析
     * @date 2023/01/19 19:55
     */
    public void invoke(List<DictionaryCode> code,boolean isNoConditional) {
        boolean sendSwitch = !(code.get(0) instanceof PlainText); //若第一行为PlainText返回false。为Function返回true
        boolean isJumpCode = false; //是否跳过解析，配合如果表达式使用

        for (DictionaryCode dic : code) {
            if (dic instanceof Expression.Return && !isJumpCode) { //兼容返回写法
                if (this instanceof FunctionRuntime) {
                    return;
                }
                clearMessage();
                return;
            }

            if (dic instanceof Expression.Else) {
                if (!isJumpCode) {
                    clearMessage();
                    return;
                }
                isJumpCode = false;
                continue;
            }

            if (isJumpCode) {
                continue;
            } else {
                exceptionStacks.push(dic.toString());
            }

            if (dic instanceof Function) {
                if (dic instanceof Function.InterruptedFunction) {
                    if (!sendSwitch) {
                        clearMessage();
                        sendSwitch = true;
                    }
                }
                int popStart = exceptionStacks.size();
                ((Function) dic).invoke(this);
                int popEnd = exceptionStacks.size();
                for (int i = popStart; i < popEnd; i++) {
                    exceptionStacks.pop(); //方法成功执行时会移除调用栈
                }
            }

            if (dic instanceof PlainText) {
                getMessageCache().append(new net.mamoe.mirai.message.data.PlainText(DictionaryUtil.cleanVariableCode(dic.getCode(), this)));
                sendSwitch = false;
            }

            if (dic instanceof FastAssignment) {
                ((FastAssignment) dic).addInRuntimeObject(this);
            }

            if (dic instanceof Expression.If) {
//                if (getMessageCache().size() != 0) {
//                    clearMessageCache();
//                    sendSwitch = true;
//                }
                Expression.If iff = (Expression.If) dic;
                if (!iff.calc(this)) {
                    isJumpCode = true;
                    continue;
                }
            }

            if (dic == code.get(code.size() - 1) && isNoConditional) {
                clearMessage();
            }
        }
        //System.out.println(exceptionStacks.toString());
    }
}
