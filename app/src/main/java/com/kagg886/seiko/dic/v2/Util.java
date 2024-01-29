package com.kagg886.seiko.dic.v2;

import com.kagg886.seiko.dic.v2.runtime.MiraiNonMessageEvent;
import io.github.seikodictionaryenginev2.base.session.BasicRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author kagg886
 * @Date 2024/1/29 上午9:01
 * @description:
 */

public class Util {
    public static Group getGroupByObjectList(BasicRuntime<?, ?, ?> runtime, List<Object> args, int start) {
        long groupId, botAccount;

        Object obj = args.get(start); //群号或上下文
        if (obj instanceof GroupMessageEvent) {
            groupId = ((GroupMessageEvent) obj).getGroup().getId();
            botAccount = ((GroupMessageEvent) obj).getBot().getId();
        } else {
            groupId = Long.parseLong(obj.toString());
            if (args.size() >= start + 2) {
                botAccount = Long.parseLong(args.get(start + 1).toString());
            } else {
                botAccount = Long.parseLong(runtime.getRuntimeObject().get("BOT").toString());
            }
        }
        return Objects.requireNonNull(Bot.findInstance(botAccount)).getGroup(groupId);
    }

    /*
     * @param runtime:
     * @param args:
     * @param start:
     * @return Info
     * @author kagg886
     * @description 根据输入参数解析群成员方法,格式为:$XXX QQ 群号 bot号$ 或 $XXX %上下文%$ 或 $XXX %集合%$
     * @date 2023/03/12 18:43
     */
    public static NormalMember getMemberInfoByObjectList(MiraiNonMessageEvent<?> runtime, List<Object> args, int start) {
        long qq, groupId, botId;
        Object obj = args.get(start); //qq或上下文
        if (obj instanceof GroupMessageEvent) {
            qq = ((GroupMessageEvent) obj).getSender().getId();
            groupId = ((GroupMessageEvent) obj).getGroup().getId();
            botId = ((GroupMessageEvent) obj).getBot().getId();
        } else if (obj instanceof Map<?, ?>) {
            qq = Long.parseLong(((Map<?, ?>) obj).get("QQ").toString());
            groupId = Long.parseLong(((Map<?, ?>) obj).get("所属群").toString());
            botId = Long.parseLong(((Map<?, ?>) obj).get("所属BOT").toString());
        } else {
            qq = Long.parseLong(obj.toString());
            if (args.size() >= start + 2) {
                groupId = Long.parseLong(args.get(start + 1).toString());
                if (args.size() >= start + 3) {
                    botId = Long.parseLong(args.get(start + 2).toString());
                } else {
                    botId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getBot().getId();
                }
            } else {
                groupId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getGroup().getId();
                botId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getBot().getId();
            }
        }
        return Bot.findInstance(botId).getGroup(groupId).get(qq);
    }
}
