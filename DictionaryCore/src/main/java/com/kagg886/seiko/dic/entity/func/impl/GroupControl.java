package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.util.DictionaryUtil;
import com.kagg886.seiko.util.IOUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.announcement.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.impl
 * @className: GroupControl
 * @author: kagg886
 * @description: 与群有关的操作
 * @date: 2023/4/10 17:05
 * @version: 1.0
 */
public abstract class GroupControl extends Function {

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.impl
     * @className: GroupControl
     * @author: kagg886
     * @description: $发布群公告 %群公告对象% 群号 bot账号(可选)$ 或 $发布群公告 %群公告对象% %上下文%$
     * @date: 2023/4/10 20:36
     * @version: 1.0
     */
    public static class PublishGroupAnnouncement extends Function.InterruptedFunction {


        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            /*{
                FID=e82ae02e000000003ad8336482c50400,
                公布时间戳=1681119290,
                属性={
                    需要确认=true,
                    发送给新成员=false,
                    置顶=false,
                    图片=https://....
                },
                发送者=485184047,
                内容=abc,√
                已确认人数=0
            }
            */
            HashMap<String,Object> map = (HashMap<String, Object>) args.get(0);
            Group group = DictionaryUtil.getGroupByObjectList(runtime,args,1);


            OfflineAnnouncement announcement;
            if (!map.containsKey("内容")) {
                throw new DictionaryOnRunningException("发布公告函数必须有内容!");
            }
            if (!map.containsKey("属性")) {
                announcement = OfflineAnnouncement.create(map.get("内容").toString());
            } else {
                HashMap<String,Object> settings = (HashMap<String, Object>) map.get("属性");
                AnnouncementParametersBuilder builder = new AnnouncementParametersBuilder();
                builder.isPinned(Boolean.parseBoolean(settings.getOrDefault("置顶","false").toString()));
                builder.sendToNewMember(Boolean.parseBoolean(settings.getOrDefault("发送给新成员","false").toString()));
                builder.requireConfirmation(Boolean.parseBoolean(settings.getOrDefault("需要确认","false").toString()));

                if (settings.containsKey("图片")) {
                    try {
                        builder.image(
                                group.getAnnouncements().uploadImage(
                                        ExternalResource.create(Jsoup.connect(
                                                        settings.get("图片").toString()
                                                ).ignoreContentType(true).execute().bodyStream()
                                        )
                                )
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                announcement = OfflineAnnouncement.create(map.get("内容").toString(),builder.build());
            }
            announcement.publishTo(group);
        }
        public PublishGroupAnnouncement(int line, String code) {
            super(line, code);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.impl
     * @className: GroupControl
     * @author: kagg886
     * @description: $删除群公告 %公告对象%$
     * @date: 2023/4/10 20:21
     * @version: 1.0
     */
    public static class DelGroupAnnouncement extends Function.InterruptedFunction {


        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object a = args.get(0);
            if (a instanceof HashMap<?,?>) {
                ((OnlineAnnouncement) ((HashMap<?, ?>) a).get("源对象")).delete();
            } else if (a instanceof OnlineAnnouncement){
                ((OnlineAnnouncement) a).delete();
            }
        }
        public DelGroupAnnouncement(int line, String code) {
            super(line, code);
        }
    }


    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.impl
     * @className: GroupControl
     * @author: kagg886
     * @description: $获取群公告 存入变量 群号 bot账号(可选)$ 或 $获取群公告 存入变量 %上下文%$
     * @date: 2023/4/10 17:20
     * @version: 1.0
     */
    public static class GetGroupAnnouncement extends Function.InterruptedFunction {


        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(0).toString();
            Group g = DictionaryUtil.getGroupByObjectList(runtime, args, 1);
            List<Object> content = new ArrayList<>();
            g.getAnnouncements().asStream().forEach((announcement) -> {
                HashMap<String,Object> unit = new HashMap<>();
                unit.put("群号",announcement.getGroup().getId());
                unit.put("内容",announcement.getContent());
                unit.put("FID",announcement.getFid());
                unit.put("发送者",announcement.getSenderId());
                unit.put("已确认人数",announcement.getConfirmedMembersCount());
                unit.put("公布时间戳",announcement.getPublicationTime());
                unit.put("源对象",announcement);

                HashMap<String,Object> props = new HashMap<>();
                AnnouncementParameters parameters = announcement.getParameters();
                props.put("发送给新成员",parameters.getSendToNewMember());
                props.put("置顶",parameters.isPinned());
                props.put("需要确认",parameters.getRequireConfirmation());
                props.put("图片","null");
                unit.put("属性",props);

                content.add(unit);
            });
            runtime.getRuntimeObject().put(putVar,content);
        }

        public GetGroupAnnouncement(int line, String code) {
            super(line, code);
        }

    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.impl
     * @className: GroupControl
     * @author: kagg886
     * @description: $退群 群号 bot账号(可选)$ 或 $退群 %上下文%$
     * @date: 2023/4/10 17:12
     * @version: 1.0
     */
    public static class Exit extends Function.InterruptedFunction {


        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Group g = DictionaryUtil.getGroupByObjectList(runtime, args, 0);
            g.quit();
        }

        public Exit(int line, String code) {
            super(line, code);
        }

    }


    public GroupControl(int line, String code) {
        super(line, code);
    }
}
