package com.kagg886.seiko.dic.v2.func;

import com.kagg886.seiko.dic.v2.runtime.MiraiMessageRuntime;
import com.kagg886.seiko.dic.v2.runtime.MiraiNonMessageEvent;
import io.github.seikodictionaryenginev2.base.entity.code.func.Function;
import io.github.seikodictionaryenginev2.base.entity.code.func.type.SendMessageWhenPostExecute;
import io.github.seikodictionaryenginev2.base.exception.DictionaryOnRunningException;
import io.github.seikodictionaryenginev2.base.session.BasicRuntime;
import net.mamoe.mirai.contact.AudioSupported;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Audio;
import net.mamoe.mirai.message.data.ShortVideo;
import net.mamoe.mirai.message.data.SuperFace;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public abstract class SingleSender {

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.interrupted
     * @className: Ptt
     * @author: kagg886
     * @description: $视频 [群/好友] (群号/好友账号) 视频链接 图片链接$ $语音 %上下文% 视频链接 图片链接$
     * @date: 2023/8/27 20:54
     * @version: 1.0
     */
    public static class Video extends Function implements SendMessageWhenPostExecute {

        public Video(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            MiraiMessageRuntime<?> r = ((MiraiMessageRuntime<?>) runtime);
            Contact supported = null;
            ShortVideo video;
            if (args.get(0).getClass().getName().equals("java.lang.String")) {
                //[群/好友] (群号/好友账号) 视频链接 图片链接
                //参数长为4，开始解析
                String a = args.get(0).toString();
                switch (a) {
                    case "群":
                        supported = r.getContact().getBot().getGroup(Long.parseLong(args.get(1).toString()));
                        break;
                    case "好友":
                        supported = r.getContact().getBot().getFriend(Long.parseLong(args.get(1).toString()));
                        break;
                }
                if (supported == null) {
                    throw new DictionaryOnRunningException("无效的" + args.get(0).toString() + ":" + args.get(1).toString());
                }
                try {
                    ExternalResource videoContent = ExternalResource.create(Jsoup.connect(args.get(2).toString()).ignoreContentType(true).execute().bodyStream());
                    ExternalResource thumbnail = ExternalResource.create(Jsoup.connect(args.get(3).toString()).ignoreContentType(true).execute().bodyStream());
                    video = supported.uploadShortVideo(thumbnail, videoContent, UUID.randomUUID() + ".mp4");
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("短视频上传失败", e);
                }
            } else {
                supported = r.getContact();
                try {
                    ExternalResource videoContent = ExternalResource.create(Jsoup.connect(args.get(1).toString()).ignoreContentType(true).execute().bodyStream());
                    ExternalResource thumbnail = ExternalResource.create(Jsoup.connect(args.get(2).toString()).ignoreContentType(true).execute().bodyStream());
                    video = supported.uploadShortVideo(thumbnail, videoContent, UUID.randomUUID() + ".mp4");
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("短视频上传失败", e);
                }
            }
            supported.sendMessage(video);
            return null;
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
    public static class Ptt extends Function implements SendMessageWhenPostExecute {
        public Ptt(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            MiraiMessageRuntime<?> r = ((MiraiMessageRuntime<?>) runtime);

            AudioSupported supported = null;
            Audio audio;
            if (args.get(0).getClass().getName().equals("java.lang.String")) {
                //参数长为4，开始解析
                String a = args.get(0).toString();
                switch (a) {
                    case "群":
                        supported = r.getContact().getBot().getGroup(Long.parseLong(args.get(1).toString()));
                        break;
                    case "好友":
                        supported = r.getContact().getBot().getFriend(Long.parseLong(args.get(1).toString()));
                        break;
                }
                if (supported == null) {
                    throw new DictionaryOnRunningException("无效的" + args.get(0).toString() + ":" + args.get(1).toString());
                }
                try {
                    audio = supported.uploadAudio(ExternalResource.create(Jsoup.connect(args.get(2).toString()).ignoreContentType(true).execute().bodyStream()));
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("语音上传失败", e);
                }
            } else {
                supported = (AudioSupported) runtime.getContact();
                try {
                    audio = supported.uploadAudio(ExternalResource.create(Jsoup.connect(args.get(1).toString()).ignoreContentType(true).execute().bodyStream()));
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("语音上传失败", e);
                }
            }
            supported.sendMessage(audio);
            return null;
        }
    }

}