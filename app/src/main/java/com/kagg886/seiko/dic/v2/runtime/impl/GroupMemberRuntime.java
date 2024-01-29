package com.kagg886.seiko.dic.v2.runtime.impl;

import com.kagg886.seiko.dic.v2.runtime.MiraiNonMessageEvent;
import io.github.seikodictionaryenginev2.base.entity.DictionaryFile;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMemberEvent;

import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: GroupMemberRuntime
 * @author: kagg886
 * @description: 群成员变动消息运行时
 * @date: 2023/3/17 8:45
 * @version: 1.0
 */
public class GroupMemberRuntime extends MiraiNonMessageEvent<GroupMemberEvent> {


    public GroupMemberRuntime(DictionaryFile file, GroupMemberEvent event) {
        super(file, event);
    }

    @Override
    protected void initObject(String command, GroupMemberEvent event) {
        super.initObject(command, event);
        Map<String, Object> context = getRuntimeObject();
        context.put("BOT", event.getBot().getId());
        context.put("群号", event.getGroup().getId());
        context.put("群名称", event.getGroup().getName());
        context.put("QQ", event.getMember().getId());
        context.put("昵称", event.getMember().getNick());
    }

    @Override
    protected Contact initContact(GroupMemberEvent groupMemberEvent) {
        return groupMemberEvent.getGroup();
    }
}
