package com.kagg886.seiko.dic.v2.func;

import com.kagg886.seiko.dic.v2.runtime.MiraiNonMessageEvent;
import io.github.seikodictionaryenginev2.base.entity.code.func.Function;
import io.github.seikodictionaryenginev2.base.exception.DictionaryOnRunningException;
import io.github.seikodictionaryenginev2.base.session.BasicRuntime;
import net.mamoe.mirai.contact.NormalMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kagg886.seiko.dic.v2.Util.getGroupByObjectList;
import static com.kagg886.seiko.dic.v2.Util.getMemberInfoByObjectList;

/**
 * @Author kagg886
 * @Date 2024/1/29 上午9:57
 * @description:
 */

public class MemberControl {

    private static HashMap<String, Object> spawnMemberInfo(NormalMember i) {
        HashMap<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("类型", "群成员");
        memberInfo.put("QQ", i.getId());
        memberInfo.put("群号", i.getGroup().getId());
        memberInfo.put("BOT", i.getBot().getId());
        memberInfo.put("昵称", i.getNick());
        memberInfo.put("群名片", i.getNameCard());
        memberInfo.put("权限", i.getPermission().toString());
        memberInfo.put("权限代码", i.getPermission().getLevel());
        memberInfo.put("特殊头衔", i.getSpecialTitle());
        memberInfo.put("加群时间戳", i.getJoinTimestamp());
        memberInfo.put("禁言剩余", i.getMuteTimeRemaining());
        memberInfo.put("最近发言时间戳", i.getLastSpeakTimestamp());
        memberInfo.put("活跃等级", i.getActive().getTemperature());
        return memberInfo;
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: ModifyAdmin
     * @author: kagg886
     * @description: $群成员 QQ号 群号 bot账号(可选)$ 或 $群成员 %上下文%$
     * @date: 2023/3/12 18:56
     * @version: 1.0
     */
    public static class GetMember extends Function {

        public GetMember(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            NormalMember i = getMemberInfoByObjectList((MiraiNonMessageEvent<?>) runtime, args, 0);
            return spawnMemberInfo(i);
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
    public static class ModifyAdmin extends Function {

        public ModifyAdmin(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            NormalMember i = getMemberInfoByObjectList((MiraiNonMessageEvent<?>) runtime, args, 0);
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
            return null;
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
    public static class ChangeTitle extends Function {

        public ChangeTitle(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            NormalMember i = getMemberInfoByObjectList(((MiraiNonMessageEvent<?>) runtime), args, 0);
            i.setSpecialTitle(args.get(0).toString());
            return null;
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
    public static class Kick extends Function {

        public Kick(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            NormalMember i = getMemberInfoByObjectList(((MiraiNonMessageEvent<?>) runtime), args, 0);
            i.kick("您已被移出本群,请重新加群");
            return null;
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
    public static class ChangeName extends Function {

        public ChangeName(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            String nick = args.get(0).toString();
            NormalMember i = getMemberInfoByObjectList(((MiraiNonMessageEvent<?>) runtime), args, 1);
            i.setNameCard(nick);
            return null;
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
    public static class Mute extends Function {

        public Mute(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            int time = Integer.parseInt(args.get(0).toString());
            NormalMember i = getMemberInfoByObjectList(((MiraiNonMessageEvent<?>) runtime), args, 1);
            if (time == 0) {
                i.unmute();
                return null;
            }
            i.mute(time);
            return null;
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: MemberList
     * @author: kagg886
     * @description: $群成员列表 群号 bot账号(可选)$ 或 $群成员列表 %上下文%$
     * @date: 2023/3/3 17:31
     * @version: 1.0
     */
    public static class MemberList extends Function {

        public MemberList(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {

            ArrayList<HashMap<String, Object>> lists = new ArrayList<>();
            for (NormalMember i : getGroupByObjectList(runtime, args, 0).getMembers()) {
                lists.add(spawnMemberInfo(i));
            }
            return lists;
        }
    }

}
