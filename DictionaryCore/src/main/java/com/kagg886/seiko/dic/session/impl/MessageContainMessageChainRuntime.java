package com.kagg886.seiko.dic.session.impl;

import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.util.stream.Collectors;

/**
 * 群消息事件处理器和好友消息事件处理器的父类
 *
 * @author kagg886
 * @date 2023/8/28 18:50
 **/
public class MessageContainMessageChainRuntime<V extends MessageEvent> extends AbsRuntime<MessageEvent> {
    public MessageContainMessageChainRuntime(DictionaryFile file, V messageEvent) {
        super(file, messageEvent);
        context.put("BOT", event.getBot().getId());

        int len = 0;
        for (SingleMessage s : event.getMessage().stream().filter(Image.class::isInstance).collect(Collectors.toList())) {
            Image t = (Image) s;
            context.put("图片" + len, Image.queryUrl(t));
            len++;
        }
        context.put("图片数", len);

        event.getMessage().forEach((sb) -> {
            if (sb instanceof OnlineAudio) {
                OnlineAudio s = (OnlineAudio) sb;
                context.put("语音链接", s.getUrlForDownload());
                context.put("语音秒数", s.getLength());
            }

            try {
                if (sb instanceof OnlineShortVideo) {
                    OnlineShortVideo s = (OnlineShortVideo) sb;
                    context.put("视频大小", s.getFileSize());
                    context.put("视频链接", s.getUrlForDownload());
                }
            } catch (NoClassDefFoundError ignored) {

            }

            if (sb instanceof QuoteReply) {
                context.put("回复者",((QuoteReply) sb).getSource().getFromId());
            }
        });
    }

    @Override
    protected Contact initContact(MessageEvent EVENT) {
        return EVENT.getSubject();
    }
}
