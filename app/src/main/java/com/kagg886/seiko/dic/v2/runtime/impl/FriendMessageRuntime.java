package com.kagg886.seiko.dic.v2.runtime.impl;

import com.kagg886.seiko.dic.v2.runtime.MiraiMessageRuntime;
import io.github.seikodictionaryenginev2.base.entity.DictionaryFile;
import net.mamoe.mirai.event.events.FriendMessageEvent;

import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: FriendMessageRuntime
 * @author: kagg886
 * @description: 好友运行时
 * @date: 2023/1/12 22:22
 * @version: 1.0
 */
public class FriendMessageRuntime extends MiraiMessageRuntime<FriendMessageEvent> {
    public FriendMessageRuntime(DictionaryFile file, FriendMessageEvent event) {
        super(file, event);
    }

    @Override
    protected void initObject(String command, FriendMessageEvent event) {
        super.initObject(command, event);

        Map<String, Object> context = getRuntimeObject();

        context.put("QQ", event.getSender().getId());
        context.put("昵称", event.getSender().getNick());
        context.put("备注", event.getSender().getRemark());
    }
}
