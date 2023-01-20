package com.kagg886.seiko.dic.session.impl;

import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.util.UnkownObject;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.OnlineAudio;
import net.mamoe.mirai.message.data.SingleMessage;

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
public class GroupMessageRuntime extends AbsRuntime<GroupMessageEvent> {

    public GroupMessageRuntime(DictionaryFile file, GroupMessageEvent event) {
        super(file, event);
        context.put("群号", new UnkownObject(event.getGroup().getId()));
        context.put("群名称", new UnkownObject(event.getGroup().getName()));

        context.put("QQ", new UnkownObject(event.getSender().getId()));
        context.put("昵称", new UnkownObject(event.getSender().getNick()));
        context.put("群名片", new UnkownObject(event.getSender().getNameCard()));
        context.put("特殊头衔", new UnkownObject(event.getSender().getSpecialTitle()));
        context.put("头衔", new UnkownObject(event.getSender().getTemperatureTitle()));
        context.put("权限", new UnkownObject(event.getSender().getPermission().toString()));
        context.put("权限代码", new UnkownObject(event.getSender().getPermission().getLevel()));

        context.put("BOT", new UnkownObject(event.getBot().getId()));

        int len = 0;
        for (SingleMessage s : event.getMessage().stream().filter(At.class::isInstance).collect(Collectors.toList())) {
            At t = (At) s;
            context.put("艾特" + len, new UnkownObject(t.getTarget()));
            len++;
        }
        context.put("艾特数", new UnkownObject(len));
        len = 0;
        for (SingleMessage s : event.getMessage().stream().filter(Image.class::isInstance).collect(Collectors.toList())) {
            Image t = (Image) s;
            context.put("图片" + len, new UnkownObject(Image.queryUrl(t)));
            len++;
        }
        context.put("图片数", new UnkownObject(len));

        if (event.getMessage().get(0) instanceof OnlineAudio) {
            OnlineAudio s = (OnlineAudio) event.getMessage().get(0);
            context.put("语音链接", new UnkownObject(s.getUrlForDownload()));
            context.put("语音秒数", new UnkownObject(s.getLength()));
        }

    }

    @Override
    public Contact getContact() {
        return event.getGroup();
    }

    @Override
    public void clearMessageCache() {
        event.getGroup().sendMessage(getMessageCache().build());
    }
}
