package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.GroupSettings;
import net.mamoe.mirai.event.events.BotPassiveEvent;
import net.mamoe.mirai.event.events.GroupAwareMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: BotControl
 * @author: kagg886
 * @description: 与Bot有关
 * @date: 2023/3/23 18:07
 * @version: 1.0
 */
public abstract class BotControl extends Function.UnInterruptedFunction {

    public BotControl(int line, String code) {
        super(line, code);
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: BotControl
     * @author: kagg886
     * @description: $获取群 存入变量 %上下文%$ 或 $获取群 存入变量 群号 bot账号$
     * @date: 2023/3/23 18:22
     * @version: 1.0
     */
    public static class getGroup extends Function.UnInterruptedFunction {

        public getGroup(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            long botId,groupId;
            Object obj = args.get(1); //可能是上下文或群号
            if (obj instanceof GroupAwareMessageEvent) {
                botId = ((GroupAwareMessageEvent) obj).getBot().getId();
                groupId = ((GroupAwareMessageEvent) obj).getGroup().getId();
            } else {
                groupId = Long.parseLong(obj.toString());
                botId = Long.parseLong(args.get(2).toString());
            }
            Group k = Bot.findInstance(botId).getGroup(groupId);

            HashMap<String, Object> hashMap = getGroupCollectionByGroup(k);
            runtime.getRuntimeObject().put(args.get(0).toString(),hashMap);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: BotControl
     * @author: kagg886
     * @description: $群列表 存入变量 %上下文%/bot账户$
     * @date: 2023/3/23 18:07
     * @version: 1.0
     */
    public static class getGroups extends Function.UnInterruptedFunction {

        public getGroups(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o1 = args.get(1);
            ContactList<Group> list;
            if (o1 instanceof BotPassiveEvent) {
                list = ((BotPassiveEvent) o1).getBot().getGroups();
            } else {
                list = Bot.findInstance(Long.parseLong(o1.toString())).getGroups();
            }
            ArrayList<HashMap<String,Object>> objects = new ArrayList<>();

            for (Group k : list) {
                objects.add(getGroupCollectionByGroup(k));
            }

            runtime.getRuntimeObject().put(args.get(0).toString(),objects);
        }
    }


    private static HashMap<String,Object> getGroupCollectionByGroup(Group k) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("BOT",k.getBot().getId());
        hashMap.put("群号",k.getId());
        hashMap.put("群名称",k.getName());
        hashMap.put("群主",k.getOwner().getId());
        GroupSettings settings = k.getSettings();
        HashMap<String,Object> content = new HashMap<>();
        content.put("允许邀请",settings.isAllowMemberInvite());
        content.put("匿名消息",settings.isAnonymousChatEnabled());
        content.put("全员禁言",settings.isMuteAll());
        content.put("自动审批",settings.isAutoApproveEnabled());
        hashMap.put("设置",content);
        return hashMap;
    }

}
