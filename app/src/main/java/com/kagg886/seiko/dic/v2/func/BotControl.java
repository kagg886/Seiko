package com.kagg886.seiko.dic.v2.func;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.seiko.dic.v2.Util;
import io.github.seikodictionaryenginev2.base.entity.code.func.Function;
import io.github.seikodictionaryenginev2.base.session.BasicRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.GroupSettings;
import net.mamoe.mirai.event.events.BotPassiveEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: BotControl
 * @author: kagg886
 * @description: 与Bot有关
 * @date: 2023/3/23 18:07
 * @version: 1.0
 */
public abstract class BotControl {
    private static Map<String, Object> getFriendCollectionByFriend(Friend k) {
        JSONObject hashMap = new JSONObject();
        hashMap.put("类型", "好友");
        hashMap.put("BOT", k.getBot().getId());
        hashMap.put("QQ", k.getId());
        hashMap.put("昵称", k.getNick());
        hashMap.put("备注", k.getRemark());
        hashMap.put("分组", k.getFriendGroup().getName());
        return hashMap;
    }

    private static HashMap<String, Object> getGroupCollectionByGroup(Group k) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("类型", "群");
        hashMap.put("BOT", k.getBot().getId());
        hashMap.put("群号", k.getId());
        hashMap.put("群名称", k.getName());
        hashMap.put("群主", k.getOwner().getId());
        GroupSettings settings = k.getSettings();
        HashMap<String, Object> content = new HashMap<>();
        content.put("允许邀请", settings.isAllowMemberInvite());
        content.put("匿名消息", settings.isAnonymousChatEnabled());
        content.put("全员禁言", settings.isMuteAll());
        content.put("自动审批", settings.isAutoApproveEnabled());
        hashMap.put("设置", content);
        return hashMap;
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: BotControl
     * @author: kagg886
     * @description: $BOT列表$
     * @date: 2023/4/21 9:05
     * @version: 1.0
     */
    public static class getBot extends Function {

        public getBot(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            return new JSONArray(Bot.getInstances().stream()
                    .filter(Bot::isOnline)
                    .map(Bot::getId)
                    .collect(Collectors.toList()));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: BotControl
     * @author: kagg886
     * @description: $获取好友 %上下文%$ 或 $获取好友 群号 bot账号$
     * @date: 2023/3/23 18:22
     * @version: 1.0
     */
    public static class getFriend extends Function {

        public getFriend(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            long botId, friendId;
            Object obj = args.get(0); //可能是上下文或群号
            if (obj instanceof MessageEvent) {
                botId = ((MessageEvent) obj).getBot().getId();
                friendId = ((MessageEvent) obj).getSender().getId();
            } else {
                friendId = Long.parseLong(obj.toString());
                botId = Long.parseLong(args.get(1).toString());
            }
            Friend k = Objects.requireNonNull(Bot.findInstance(botId)).getFriend(friendId);

            return getFriendCollectionByFriend(Objects.requireNonNull(k));
//            runtime.getRuntimeObject().put(args.get(0).toString(), hashMap);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: BotControl
     * @author: kagg886
     * @description: $好友列表 %上下文%/bot账户$
     * @date: 2023/3/24 8:40
     * @version: 1.0
     */
    public static class getFriends extends Function {

        public getFriends(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            Object o1 = args.get(0);
            ContactList<Friend> list;
            if (o1 instanceof BotPassiveEvent) {
                list = ((BotPassiveEvent) o1).getBot().getFriends();
            } else {
                list = Bot.findInstance(Long.parseLong(o1.toString())).getFriends();
            }
            JSONArray objects = new JSONArray();

            for (Friend k : list) {
                objects.add(getFriendCollectionByFriend(k));
            }
            return objects;
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: BotControl
     * @author: kagg886
     * @description: $获取群 %上下文%$ 或 $获取群 群号 bot账号$
     * @date: 2023/3/23 18:22
     * @version: 1.0
     */
    public static class getGroup extends Function {

        public getGroup(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            return getGroupCollectionByGroup(
                    Util.getGroupByObjectList(runtime, args, 0)
            );
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: BotControl
     * @author: kagg886
     * @description: $群列表 %上下文%/bot账户$
     * @date: 2023/3/23 18:07
     * @version: 1.0
     */
    public static class getGroups extends Function {

        public getGroups(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            Object o1 = args.get(0);
            ContactList<Group> list;
            if (o1 instanceof BotPassiveEvent) {
                list = ((BotPassiveEvent) o1).getBot().getGroups();
            } else {
                list = Bot.findInstance(Long.parseLong(o1.toString())).getGroups();
            }
            ArrayList<HashMap<String, Object>> objects = new ArrayList<>();

            for (Group k : list) {
                objects.add(getGroupCollectionByGroup(k));
            }

            return objects;
        }
    }

}
