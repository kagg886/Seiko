package com.kagg886.seiko.dic.session.impl;

import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMemberEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: GroupMemberRuntime
 * @author: kagg886
 * @description: 群成员变动消息运行时
 * @date: 2023/3/17 8:45
 * @version: 1.0
 */
public class GroupMemberRuntime extends AbsRuntime<GroupMemberEvent> {


    public GroupMemberRuntime(DictionaryFile file, GroupMemberEvent event) {
        super(file, event);
        context.put("BOT",event.getBot().getId());
        context.put("群号", event.getGroup().getId());
        context.put("群名称", event.getGroup().getName());
        context.put("QQ", event.getMember().getId());
        context.put("昵称",event.getMember().getNick());
    }

    @Override
    public Contact getContact() {
        return event.getMember();
    }

    @Override
    protected void clearMessageCache() {
        event.getGroup().sendMessage(getMessageCache().build());
    }
}
