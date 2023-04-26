package com.kagg886.seiko.dic.session.impl;

import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;

import java.util.Objects;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: MemberJoinRequestRuntime
 * @author: kagg886
 * @description: 成员进群时的Runtime，与GroupMemberRuntime保持同步
 * @date: 2023/4/26 20:42
 * @version: 1.0
 */
public class MemberJoinRequestRuntime extends AbsRuntime<MemberJoinRequestEvent> {
    public MemberJoinRequestRuntime(DictionaryFile file, MemberJoinRequestEvent event) {
        super(file, event);
        context.put("BOT",event.getBot().getId());
        context.put("群号", Objects.requireNonNull(event.getGroup()).getId());
        context.put("群名称", event.getGroup().getName());
        context.put("QQ", event.getFromId());
        context.put("昵称",event.getFromNick());
        context.put("文本",event.getMessage());
    }

    @Override
    protected Contact initContact(MemberJoinRequestEvent EVENT) {
        return EVENT.getGroup();
    }
}
