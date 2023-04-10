package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.util.DictionaryUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.announcement.AnnouncementParameters;

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

                unit.put("内容",announcement.getContent());
                unit.put("FID",announcement.getFid());
                unit.put("发送者",announcement.getSenderId());
                unit.put("已确认人数",announcement.getConfirmedMembersCount());
                unit.put("公布时间戳",announcement.getPublicationTime());

                HashMap<String,Object> props = new HashMap<>();
                AnnouncementParameters parameters = announcement.getParameters();
                props.put("发送给新成员",parameters.getSendToNewMember());
                props.put("置顶",parameters.isPinned());
                props.put("需要确认",parameters.getRequireConfirmation());
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
