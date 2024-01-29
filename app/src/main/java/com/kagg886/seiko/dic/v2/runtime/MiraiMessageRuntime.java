package com.kagg886.seiko.dic.v2.runtime;

import io.github.seikodictionaryenginev2.base.entity.DictionaryFile;
import io.github.seikodictionaryenginev2.base.session.BasicRuntime;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

/**
 * @Author kagg886
 * @Date 2024/1/29 上午8:30
 * @description:
 */

public abstract class MiraiMessageRuntime<T extends MessageEvent> extends MiraiNonMessageEvent<T> {
    public MiraiMessageRuntime(DictionaryFile file, T t) {
        super(file, t);
    }

    @Override
    protected Contact initContact(T t) {
        return t.getSubject();
    }
}
