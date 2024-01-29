package com.kagg886.seiko.dic.v2.runtime;

import io.github.seikodictionaryenginev2.base.entity.DictionaryFile;
import io.github.seikodictionaryenginev2.base.session.BasicRuntime;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.data.MessageChainBuilder;

/**
 * @Author kagg886
 * @Date 2024/1/29 上午8:38
 * @description:
 */

public abstract class MiraiNonMessageEvent<T extends Event> extends BasicRuntime<T, Contact, MessageChainBuilder> {

    public MiraiNonMessageEvent(DictionaryFile file, T t) {
        super(file, t);
    }


    @Override
    protected void initObject(String command, T t) {
        super.initObject(command, t);
        getRuntimeObject().put("上下文", t);
    }

    @Override
    protected MessageChainBuilder initMessageCache() {
        return new MessageChainBuilder();
    }

    @Override
    protected void clearMessage0(MessageChainBuilder singleMessages) {
        getContact().sendMessage(singleMessages.build());
    }

    @Override
    protected void appendMessage(String s) {
        getMessageCache().append(s);
    }
}
