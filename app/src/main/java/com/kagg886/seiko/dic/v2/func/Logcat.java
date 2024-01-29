package com.kagg886.seiko.dic.v2.func;

import com.kagg886.seiko.dic.v2.runtime.MiraiNonMessageEvent;
import io.github.seikodictionaryenginev2.base.entity.code.func.Function;
import io.github.seikodictionaryenginev2.base.entity.code.func.type.ArgumentLimiter;
import io.github.seikodictionaryenginev2.base.exception.DictionaryOnRunningException;
import io.github.seikodictionaryenginev2.base.session.BasicRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.BotPassiveEvent;
import net.mamoe.mirai.utils.SimpleLogger;

import java.util.List;

/**
 * @Author kagg886
 * @Date 2024/1/29 上午9:30
 * @description:
 */

public class Logcat extends Function implements ArgumentLimiter {

    public Logcat(int line, String code) {
        super(line, code);
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
    protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
        callLogger(((BotPassiveEvent) runtime.getRuntimeObject().get("上下文")).getBot(), args);
        return null;
    }

    @Override
    public int getArgumentLength() {
        return 2;
    }
}
