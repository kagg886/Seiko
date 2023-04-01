package com.kagg886.seiko.dic.session.impl;

import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.FriendMessageEvent;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: FriendMessageRuntime
 * @author: kagg886
 * @description: 好友运行时
 * @date: 2023/1/12 22:22
 * @version: 1.0
 */
public class FriendMessageRuntime extends AbsRuntime<FriendMessageEvent> {
    public FriendMessageRuntime(DictionaryFile file, FriendMessageEvent event) {
        super(file, event);
        context.put("QQ", event.getSender().getId());
        context.put("昵称", event.getSender().getNick());
        context.put("BOT", event.getBot().getId());
        context.put("备注", event.getSender().getRemark());
    }

    @Override
    protected Contact initContact(FriendMessageEvent friendMessageEvent) {
        return friendMessageEvent.getFriend();
    }
}
