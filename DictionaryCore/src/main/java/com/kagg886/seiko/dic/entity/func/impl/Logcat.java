package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.BotPassiveEvent;
import net.mamoe.mirai.utils.SimpleLogger;

import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.impl
 * @className: Logcat
 * @author: kagg886
 * @description: $日志 优先级 信息$
 * @date: 2023/4/12 7:57
 * @version: 1.0
 */
public class Logcat extends Function.UnInterruptedFunction implements Function.ArgumentLimiter {

    public Logcat(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        if (DictionaryEnvironment.getInstance().isShowLogOnAllBots()) {
            for (Bot b : Bot.getInstances()) {
                callLogger(b, args);
            }
        } else {
            callLogger(((BotPassiveEvent) runtime.getRuntimeObject().get("上下文")).getBot(), args);
        }
    }

    public static void callLogger(Bot b, List<Object> args) {
        SimpleLogger.LogPriority prop = null;
        Object obj = args.get(0);
        try {
            int i = Integer.parseInt(obj.toString());
            prop = SimpleLogger.LogPriority.values()[i];
        } catch (NumberFormatException e) {
            for (SimpleLogger.LogPriority p : SimpleLogger.LogPriority.values()) {
                if (p.getNameAligned().equals(obj.toString()) || p.getSimpleName().equals(obj.toString())) {
                    prop = p;
                    break;
                }
            }
        }
        if (prop == null) {
            throw new DictionaryOnRunningException("日志等级标识错误:" + obj);
        }
        b.getLogger().call(prop, args.get(1).toString(), null);
    }

    @Override
    public int getArgumentLength() {
        return 2;
    }
}
