package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.contact.AudioSupported;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Audio;
import net.mamoe.mirai.message.data.ShortVideo;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public abstract class SingleSender extends Function.InterruptedFunction {

    public SingleSender(int line, String code) {
        super(line, code);
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.interrupted
     * @className: Ptt
     * @author: kagg886
     * @description: $视频 [群/好友] (群号/好友账号) 视频链接 图片链接$ $语音 %上下文% 视频链接 图片链接$
     * @date: 2023/8/27 20:54
     * @version: 1.0
     */
    public static class Video extends SingleSender {

        public Video(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Contact supported = null;
            ShortVideo video;
            if (args.get(0).getClass().getName().equals("java.lang.String")) {
                //[群/好友] (群号/好友账号) 视频链接 图片链接
                //参数长为4，开始解析
                String a = args.get(0).toString();
                switch (a) {
                    case "群":
                        supported = runtime.getContact().getBot().getGroup(Long.parseLong(args.get(1).toString()));
                        break;
                    case "好友":
                        supported = runtime.getContact().getBot().getFriend(Long.parseLong(args.get(1).toString()));
                        break;
                }
                if (supported == null) {
                    throw new DictionaryOnRunningException("无效的" + args.get(0).toString() + ":" + args.get(1).toString());
                }
                try {
                    ExternalResource videoContent = ExternalResource.create(Jsoup.connect(args.get(2).toString()).ignoreContentType(true).execute().bodyStream());
                    ExternalResource thumbnail = ExternalResource.create(Jsoup.connect(args.get(3).toString()).ignoreContentType(true).execute().bodyStream());
                    video = supported.uploadShortVideo(thumbnail,videoContent, UUID.randomUUID() + ".mp4");
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("短视频上传失败",e);
                }
            } else {
                supported = runtime.getContact();
                try {
                    ExternalResource videoContent = ExternalResource.create(Jsoup.connect(args.get(1).toString()).ignoreContentType(true).execute().bodyStream());
                    ExternalResource thumbnail = ExternalResource.create(Jsoup.connect(args.get(2).toString()).ignoreContentType(true).execute().bodyStream());
                    video = supported.uploadShortVideo(thumbnail,videoContent, UUID.randomUUID() + ".mp4");
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("短视频上传失败",e);
                }
            }
            supported.sendMessage(video);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.interrupted
     * @className: Ptt
     * @author: kagg886
     * @description: $语音 [群/好友] (群号/好友账号) 语音链接$ $语音 %上下文% 语音链接$
     * @date: 2023/1/29 9:23
     * @version: 1.0
     */
    public static class Ptt extends SingleSender {
        public Ptt(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            AudioSupported supported = null;
            Audio audio;
            if (args.get(0).getClass().getName().equals("java.lang.String")) {
                //参数长为4，开始解析
                String a = args.get(0).toString();
                switch (a) {
                    case "群":
                        supported = runtime.getContact().getBot().getGroup(Long.parseLong(args.get(1).toString()));
                        break;
                    case "好友":
                        supported = runtime.getContact().getBot().getFriend(Long.parseLong(args.get(1).toString()));
                        break;
                }
                if (supported == null) {
                    throw new DictionaryOnRunningException("无效的" + args.get(0).toString() + ":" + args.get(1).toString());
                }
                try {
                    audio = supported.uploadAudio(ExternalResource.create(Jsoup.connect(args.get(2).toString()).ignoreContentType(true).execute().bodyStream()));
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("语音上传失败",e);
                }
            } else {
                supported = (AudioSupported) runtime.getContact();
                try {
                    audio = supported.uploadAudio(ExternalResource.create(Jsoup.connect(args.get(1).toString()).ignoreContentType(true).execute().bodyStream()));
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("语音上传失败",e);
                }
            }
            supported.sendMessage(audio);
        }
    }

}