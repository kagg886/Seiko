package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kagg886.seiko.dic.DictionaryUtil.getMemberInfoByObjectList;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: MemberControl
 * @author: kagg886
 * @description: 群成员相关函数
 * @date: 2023/3/3 17:29
 * @version: 1.0
 */
public abstract class MemberControl extends Function.UnInterruptedFunction {


    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Kick
     * @author: kagg886
     * @description: $群头衔 头衔名 QQ 群号(可选) bot账号(可选)$ 或 $群头衔 头衔名 %集合对象%$ 或 $群头衔 头衔名 %上下文%$
     * @date: 2023/3/12 18:56
     * @version: 1.0
     */
    public static class ChangeTitle extends MemberControl {

        public ChangeTitle(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            NormalMember i = getMemberInfoByObjectList(runtime, args, 1);
            i.setSpecialTitle(args.get(0).toString());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Kick
     * @author: kagg886
     * @description: $踢 QQ 群号(可选) bot账号(可选)$ 或 $踢 %集合对象%$ 或 $踢 %上下文%$
     * @date: 2023/3/7 19:16
     * @version: 1.0
     */
    public static class Kick extends MemberControl {

        public Kick(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            NormalMember i = getMemberInfoByObjectList(runtime, args, 0);
            i.kick("您已被移出本群,请重新加群");
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: ChangeName
     * @author: kagg886
     * @description: $改名 新的群名片 QQ 群号(可选) bot账号(可选)$ 或 $改名 新的群名片 %上下文%$ 或 $改名 新的群名片 集合对象$
     * @date: 2023/3/12 18:54
     * @version: 1.0
     */
    public static class ChangeName extends MemberControl {

        public ChangeName(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String nick = args.get(0).toString();
            NormalMember i = getMemberInfoByObjectList(runtime, args, 1);
            i.setNameCard(nick);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Mute
     * @author: kagg886
     * @description: $禁言 时长(秒为单位) QQ 群号(可选) bot账号(可选)$ 或 $禁言 时长(秒为单位) %上下文%$ 或 $禁言 时长(秒为单位) 集合对象$
     * @date: 2023/3/6 20:30
     * @version: 1.0
     */
    public static class Mute extends MemberControl {

        public Mute(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            int time = Integer.parseInt(args.get(0).toString());
            NormalMember i = getMemberInfoByObjectList(runtime, args, 1);
            if (time == 0) {
                i.unmute();
                return;
            }
            i.mute(time);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: MemberList
     * @author: kagg886
     * @description: $群成员列表 存入变量 群号 bot账号(可选)$ 或 $群成员列表 存入变量 %上下文%$
     * @date: 2023/3/3 17:31
     * @version: 1.0
     */
    public static class MemberList extends MemberControl {

        public MemberList(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(0).toString();
            long groupId, botAccount;

            Object obj = args.get(1); //群号或上下文
            if (obj instanceof GroupMessageEvent) {
                groupId = ((GroupMessageEvent) obj).getGroup().getId();
                botAccount = ((GroupMessageEvent) obj).getBot().getId();
            } else {
                groupId = Long.parseLong(obj.toString());
                if (args.size() >= 3) {
                    botAccount = Long.parseLong(args.get(2).toString());
                } else {
                    botAccount = Long.parseLong(runtime.getRuntimeObject().get("BOT").toString());
                }
            }
            ArrayList<HashMap<String, String>> lists = new ArrayList<>();
            for (NormalMember i : Bot.findInstance(botAccount).getGroup(groupId).getMembers()) {
                HashMap<String, String> memberInfo = new HashMap<>();
                memberInfo.put("QQ", String.valueOf(i.getId()));
                memberInfo.put("所属群", String.valueOf(i.getGroup().getId()));
                memberInfo.put("所属BOT", String.valueOf(i.getBot().getId()));
                memberInfo.put("昵称", i.getNick());
                memberInfo.put("群名片", i.getNameCard());
                memberInfo.put("权限", i.getPermission().toString());
                memberInfo.put("权限代码", String.valueOf(i.getPermission().getLevel()));
                memberInfo.put("特殊头衔", i.getSpecialTitle());
                memberInfo.put("加群时间戳", String.valueOf(i.getJoinTimestamp()));
                memberInfo.put("禁言剩余", String.valueOf(i.getMuteTimeRemaining()));
                memberInfo.put("最近发言时间戳", String.valueOf(i.getLastSpeakTimestamp()));
                lists.add(memberInfo);
            }
            runtime.getRuntimeObject().put(putVar, lists);
        }
    }

    public MemberControl(int line, String code) {
        super(line, code);
    }
}
