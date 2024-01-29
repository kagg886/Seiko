package com.kagg886.seiko.dic;

import com.alibaba.fastjson2.JSONObject;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.model.DICParseResult;
import com.kagg886.seiko.dic.session.impl.FriendMessageRuntime;
import com.kagg886.seiko.dic.session.impl.GroupMemberRuntime;
import com.kagg886.seiko.dic.session.impl.GroupMessageRuntime;
import com.kagg886.seiko.dic.session.impl.MemberJoinRequestRuntime;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.utils.MiraiLogger;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic
 * @className: DictionaryReg
 * @author: kagg886
 * @description: 注册Seiko逻辑的公共代码
 * @date: 2023/4/26 20:11
 * @version: 1.0
 */
public class DictionaryReg {
    public static void reg(EventChannel<?> channel, boolean refreshDIC, CallBack call) {

        channel.subscribeAlways(GroupMessageEvent.class, event -> { //注册群消息
            if (refreshDIC) {
                DICParseResult res = DICList.INSTANCE.refresh();
                if (!res.success) {
                    call.call(event.getBot().getLogger(), res);
                    return;
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.INSTANCE) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dic.getName());
                Boolean bo = dicConfigUnit.getBoolean("enabled");
                if (bo == null) {
                    bo = true;
                }
                if (!bo) {
                    return;
                }
                GroupMessageRuntime runtime = new GroupMessageRuntime(dic, event);
                runtime.invoke(event.getMessage().contentToString());
            }
        });

        channel.subscribeAlways(FriendMessageEvent.class, event -> { //注册好友消息
            if (refreshDIC) {
                DICParseResult res = DICList.INSTANCE.refresh();
                if (!res.success) {
                    call.call(event.getBot().getLogger(), res);
                    return;
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.INSTANCE) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dic.getName());
                Boolean bo = dicConfigUnit.getBoolean("enabled");
                if (bo == null) {
                    bo = true;
                }
                if (!bo) {
                    return;
                }
                FriendMessageRuntime runtime = new FriendMessageRuntime(dic, event);
                runtime.invoke(event.getMessage().contentToString());
            }


        });

        channel.subscribeAlways(GroupMemberEvent.class, event -> { //注册成员被踢，主动退群消息 被邀请 主动入群和恢复解散群
            if (refreshDIC) {
                DICParseResult res = DICList.INSTANCE.refresh();
                if (!res.success) {
                    call.call(event.getBot().getLogger(), res);
                    return;
                }
            }

            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.INSTANCE) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dic.getName());
                Boolean bo = dicConfigUnit.getBoolean("enabled");
                if (bo == null) {
                    bo = true;
                }
                if (!bo) {
                    return;
                }
                GroupMemberRuntime runtime = new GroupMemberRuntime(dic, event);
                if (event instanceof MemberPermissionChangeEvent) {
                    runtime.getRuntimeObject().put("原权限",((MemberPermissionChangeEvent) event).getOrigin().toString());
                    runtime.getRuntimeObject().put("原权限代码",((MemberPermissionChangeEvent) event).getOrigin().getLevel());
                    runtime.getRuntimeObject().put("现权限",((MemberPermissionChangeEvent) event).getNew().toString());
                    runtime.getRuntimeObject().put("现权限代码",((MemberPermissionChangeEvent) event).getNew().getLevel());
                    runtime.invoke("成员权限改变");
                    return;
                }

                if (event instanceof MemberLeaveEvent.Kick) {
                    runtime.getRuntimeObject().put("操作人", ((MemberLeaveEvent.Kick) event).getOperator().getId());
                    runtime.invoke("成员被踢");
                    return;
                }
                if (event instanceof MemberLeaveEvent.Quit) {
                    runtime.invoke("成员主动退群");
                    return;
                }

                if (event instanceof MemberJoinEvent.Invite) {
                    runtime.getRuntimeObject().put("邀请人", ((MemberJoinEvent.Invite) event).getInvitor().getId());
                    runtime.invoke("成员邀请入群");
                    return;
                }

                if (event instanceof MemberJoinEvent.Active) {
                    runtime.invoke("成员主动入群");
                    return;
                }

                if (event instanceof MemberJoinEvent.Retrieve) {
                    runtime.invoke("群主恢复解散群");
                    return;
                }
            }
        });

        channel.subscribeAlways(MemberJoinRequestEvent.class, event -> {
            if (refreshDIC) {
                DICParseResult res = DICList.INSTANCE.refresh();
                if (!res.success) {
                    call.call(event.getBot().getLogger(), res);
                    return;
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.INSTANCE) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dic.getName());
                Boolean bo = dicConfigUnit.getBoolean("enabled");
                if (bo == null) {
                    bo = true;
                }
                if (!bo) {
                    return;
                }
                MemberJoinRequestRuntime runtime = new MemberJoinRequestRuntime(dic, event);
                runtime.invoke("申请入群");
            }
        });
    }

    @FunctionalInterface
    public interface CallBack {
        void call(MiraiLogger logger, DICParseResult result);
    }
}
