package com.kagg886.seiko.dic.v2.runtime.impl;

import com.kagg886.seiko.dic.v2.runtime.MiraiMessageRuntime;
import com.kagg886.seiko.util.TextUtils;
import io.github.seikodictionaryenginev2.base.entity.DictionaryFile;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: GroupMessageRuntime
 * @author: kagg886
 * @description: 群消息运行时
 * @date: 2023/1/12 21:26
 * @version: 1.0
 */
public class GroupMessageRuntime extends MiraiMessageRuntime<GroupMessageEvent> {
    public GroupMessageRuntime(DictionaryFile file, GroupMessageEvent groupMessageEvent) {
        super(file, groupMessageEvent);
    }

    @Override
    protected void initObject(String command, GroupMessageEvent event) {
        super.initObject(command, event);
        Map<String, Object> context = getRuntimeObject();

        context.put("群号", event.getGroup().getId());
        context.put("群名称", event.getGroup().getName());

        context.put("QQ", event.getSender().getId());

        String nick = event.getSender().getNick();
        String nameCard = event.getSender().getNameCard();
        context.put("昵称", nick);
        if (TextUtils.isEmpty(nameCard)) {
            context.put("群名片", nick);
        } else {
            context.put("群名片", nameCard); //保证没有获取到群名片的人能正常获取到群名片
        }

        context.put("特殊头衔", event.getSender().getSpecialTitle());
        context.put("头衔", event.getSender().getTemperatureTitle());
        context.put("权限", event.getSender().getPermission().toString());
        context.put("权限代码", event.getSender().getPermission().getLevel());


        int len = 0;
        for (SingleMessage s : event.getMessage().stream().filter(At.class::isInstance).collect(Collectors.toList())) {
            At t = (At) s;
            context.put("艾特" + len, t.getTarget());
            len++;
        }
        context.put("艾特数", len);
    }
}
