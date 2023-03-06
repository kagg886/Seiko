package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * @className: Random
     * @author: kagg886
     * @description: $禁言 时长(秒为单位) QQ 群号(可选) bot账号(可选)$ 或 $禁言 时长(秒为单位) %上下文%$ 或 $禁言 时长(秒为单位) 集合对象$
     * @date: 2023/3/3 17:31
     * @version: 1.0
     */
    public static class Mute extends Function.UnInterruptedFunction {

        public Mute(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            int time = Integer.parseInt(args.get(0).toString());
            Object obj = args.get(1); //QQ或上下文
            long qq,groupId,botId;
            if (obj instanceof GroupMessageEvent) {
                qq = ((GroupMessageEvent) obj).getSender().getId();
                groupId = ((GroupMessageEvent) obj).getGroup().getId();
                botId = ((GroupMessageEvent) obj).getBot().getId();
            } else if (obj instanceof HashMap<?, ?>) {
                qq = Long.parseLong(((HashMap<?, ?>) obj).get("QQ").toString());
                groupId = Long.parseLong(((HashMap<?, ?>) obj).get("所属群").toString());
                botId = Long.parseLong(((HashMap<?, ?>) obj).get("所属BOT").toString());
            } else {
                qq = Long.parseLong(obj.toString());
                if (args.size() >= 3) {
                    groupId = Long.parseLong(args.get(2).toString());
                    if (args.size() >= 4) {
                        botId = Long.parseLong(args.get(3).toString());
                    } else {
                        botId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getBot().getId();
                    }
                } else {
                    groupId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getGroup().getId();
                    botId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getBot().getId();
                }
            }
            NormalMember i = Bot.findInstance(botId).getGroup(groupId).get(qq);
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
     * @className: Random
     * @author: kagg886
     * @description: $群成员列表 存入变量 群号 bot账号(可选)$ 或 $群成员列表 存入变量 %上下文%$
     * @date: 2023/3/3 17:31
     * @version: 1.0
     */
    public static class MemberList extends Function.UnInterruptedFunction {

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
            ArrayList<HashMap<String,String>> lists = new ArrayList<>();
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
            runtime.getRuntimeObject().put(putVar,lists);
        }
    }

    public MemberControl(int line, String code) {
        super(line, code);
    }

}
