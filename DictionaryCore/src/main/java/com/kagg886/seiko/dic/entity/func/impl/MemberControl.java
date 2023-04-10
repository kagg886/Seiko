package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.util.DictionaryUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kagg886.seiko.dic.util.DictionaryUtil.getMemberInfoByObjectList;

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
     * @className: ModifyAdmin
     * @author: kagg886
     * @description: $群成员 存入变量 QQ号 群号 bot账号(可选)$ 或 $群成员 存入变量 %上下文%$
     * @date: 2023/3/12 18:56
     * @version: 1.0
     */
    public static class GetMember extends MemberControl {

        public GetMember(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            NormalMember i = getMemberInfoByObjectList(runtime, args, 1);
            String s = args.get(0).toString();
            HashMap<String, Object> memberInfo = spawnMemberInfo(i);
            runtime.getRuntimeObject().put(s,memberInfo);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: ModifyAdmin
     * @author: kagg886
     * @description: $管理员 上/下 QQ 群号(可选) bot账号(可选)$ 或 $管理员 上/下 %集合对象%$ 或 $管理员 上/下 %上下文%$
     * @date: 2023/3/12 18:56
     * @version: 1.0
     */
    public static class ModifyAdmin extends MemberControl {

        public ModifyAdmin(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            NormalMember i = getMemberInfoByObjectList(runtime, args, 1);
            String s = args.get(0).toString();
            boolean x;
            if (s.equals("上")) {
                x = true;
            } else if (s.equals("下")) {
                x = false;
            } else {
                throw new DictionaryOnRunningException("未知状态:" + s);
            }
            i.modifyAdmin(x);
        }
    }


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

            ArrayList<HashMap<String, Object>> lists = new ArrayList<>();
            for (NormalMember i : DictionaryUtil.getGroupByObjectList(runtime,args,1).getMembers()) {
                lists.add(spawnMemberInfo(i));
            }
            runtime.getRuntimeObject().put(putVar, lists);
        }
    }

    private static HashMap<String, Object> spawnMemberInfo(NormalMember i) {
        HashMap<String,Object> memberInfo = new HashMap<>();
        memberInfo.put("类型","群成员");
        memberInfo.put("QQ", i.getId());
        memberInfo.put("群号", i.getGroup().getId());
        memberInfo.put("BOT", i.getBot().getId());
        memberInfo.put("昵称", i.getNick());
        memberInfo.put("群名片", i.getNameCard());
        memberInfo.put("权限", i.getPermission().toString());
        memberInfo.put("权限代码", i.getPermission().getLevel());
        memberInfo.put("特殊头衔", i.getSpecialTitle());
        memberInfo.put("加群时间戳", i.getJoinTimestamp());
        memberInfo.put("禁言剩余",i.getMuteTimeRemaining());
        memberInfo.put("最近发言时间戳", i.getLastSpeakTimestamp());
        return memberInfo;
    }

    public MemberControl(int line, String code) {
        super(line, code);
    }
}
