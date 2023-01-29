package com.kagg886.seiko.dic.entity.func.interrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.contact.AudioSupported;
import net.mamoe.mirai.message.data.Audio;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.interrupted
 * @className: Ptt
 * @author: kagg886
 * @description: $语音 [群/好友] (群号/好友账号) 语音链接$ $语音 %上下文% 语音链接$
 * @date: 2023/1/29 9:23
 * @version: 1.0
 */
public class Ptt extends Function.InterruptedFunction {
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
                throw new DictionaryOnRunningException("语音上传失败");
            }
        } else {
            supported = (AudioSupported) runtime.getContact();
            try {
                audio = supported.uploadAudio(ExternalResource.create(Jsoup.connect(args.get(1).toString()).ignoreContentType(true).execute().bodyStream()));
            } catch (IOException e) {
                throw new DictionaryOnRunningException("语音上传失败");
            }
        }
        supported.sendMessage(audio);
    }
}
