package com.kagg886.seiko.dic.session;

import com.kagg886.seiko.dic.DictionaryUtil;
import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.DictionaryCommandMatcher;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.entity.impl.Expression;
import com.kagg886.seiko.dic.entity.impl.PlainText;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.impl.FunctionRuntime;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
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
public abstract class AbsRuntime<T> {
    protected final T event; //此次执行伪代码所需要的事件
    protected DictionaryFile file; //被执行的伪代码指令集
    protected HashMap<String, Object> context; //此次伪代码执行过程中存取的变量
    protected Stack<String> exceptionStacks; //词库调用栈，每次*执行完一条命令*就会存储一条信息到栈中。

    /*
     * @param file: 需要执行的dicFile
     * @param event: 此次执行伪代码所需要的事件
     * @author kagg886
     * @description 构造函数
     * @date 2023/01/19 19:53
     */
    public AbsRuntime(DictionaryFile file, T event) {
        this.file = file;
        this.event = event;
        context = new HashMap<>();
        exceptionStacks = new Stack<>();

        //通用的变量会存储在这里。
        context.put("上下文", event);
        context.put("缓冲区", new MessageChainBuilder());
        context.put("时间戳", System.currentTimeMillis());
    }

    public Stack<String> getExceptionStacks() {
        return exceptionStacks;
    }

    public DictionaryFile getFile() {
        return file;
    }

    public abstract Contact getContact();

    public MessageChainBuilder getMessageCache() {
        return (MessageChainBuilder) context.get("缓冲区");
    }

    protected abstract void clearMessageCache(); //抽象的清空缓冲区方法

    public void clearMessage() { //清空缓冲区，之所以如此设计是因为不同事件发送消息的方法是不同的
        if (getMessageCache().size() == 0) {
            return;
        }
        clearMessageCache();
        context.put("缓冲区", new MessageChainBuilder());
    }

    public HashMap<String, Object> getRuntimeObject() {
        return context;
    }

    /*
     * @param command: 指令
     * @return void
     * @author kagg886
     * @description 暴露在外部的invoke，用于匹配合适的伪代码代码并送到内部invoke函数
     * @date 2023/01/19 19:54
     */
    public void invoke(String command) {
        for (Map.Entry<DictionaryCommandMatcher, ArrayList<DictionaryCode>> entry : file.getCommands().entrySet()) {
            DictionaryCommandMatcher matcher = entry.getKey();
            ArrayList<DictionaryCode> code = entry.getValue();
            if (!matcher.matchesDomain(event)) { //匹配指令触发的环境和当前环境是否相符
                continue;
            }
            if (matcher.matchesCommand(command)) { //正则匹配
                String[] x = command.split(" ");
                context.put("文本", command);
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
            }
            try {
                invoke(code);
            } catch (Exception e) { //异常处理，生成调用栈信息向上抛出
                String msg = e.getMessage();
                if (e instanceof DictionaryOnRunningException) {
                    msg = ((DictionaryOnRunningException) e).getMsg();
                }
                throw new DictionaryOnRunningException(file, msg, exceptionStacks, e);
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
    private void invoke(ArrayList<DictionaryCode> code) {
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

            if (dic instanceof Expression.If) {
                if (getMessageCache().size() != 0) {
                    clearMessageCache();
                    sendSwitch = true;
                }
                Expression.If iff = (Expression.If) dic;
                if (!iff.calc(this)) {
                    isJumpCode = true;
                    continue;
                }
            }

            if (dic == code.get(code.size() - 1)) {
                clearMessage();
            }
        }
        //System.out.println(exceptionStacks.toString());
    }
}
