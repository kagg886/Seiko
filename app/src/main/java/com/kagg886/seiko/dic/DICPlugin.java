package com.kagg886.seiko.dic;

import android.content.Context;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.dic.v2.runtime.impl.FriendMessageRuntime;
import com.kagg886.seiko.dic.v2.runtime.impl.GroupMemberRuntime;
import com.kagg886.seiko.dic.v2.runtime.impl.GroupMessageRuntime;
import com.kagg886.seiko.dic.v2.runtime.impl.MemberJoinRequestRuntime;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import io.github.seikodictionaryenginev2.base.command.Registrator;
import io.github.seikodictionaryenginev2.base.entity.DictionaryFile;
import io.github.seikodictionaryenginev2.base.entity.DictionaryProject;
import io.github.seikodictionaryenginev2.base.entity.code.func.Function;
import io.github.seikodictionaryenginev2.base.env.DICList;
import io.github.seikodictionaryenginev2.base.env.DictionaryEnvironment;
import io.github.seikodictionaryenginev2.base.model.DICParseResult;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic
 * @className: DICPlugin
 * @author: kagg886
 * @description: SeikoDIC插件入口
 * @date: 2023/1/9 17:31
 * @version: 1.0
 */
public class DICPlugin extends SeikoPlugin {
    private static final String[][] dicFunctions = {
            {"BOT列表", "BotControl$getBot"},
            {"群列表", "BotControl$getGroups"},
            {"获取群", "BotControl$getGroup"},
            {"好友列表", "BotControl$getFriends"},
            {"获取好友", "BotControl$getFriend"},


            {"日志", "Logcat"},

            {"图片", "MessageControl$addImage"},
            {"艾特", "MessageControl$addAt"},
            {"撤回", "MessageControl$Recall"},
            {"设置回复", "MessageControl$Reply"},
            {"设置接收者", "MessageControl$setSender"},
            {"戳", "MessageControl$Nudge"},
            {"语音", "SingleSender$Ptt"},
            {"视频", "SingleSender$Video"},

            {"群成员", "MemberControl$GetMember"},
            {"群成员列表", "MemberControl$MemberList"},
            {"改名", "MemberControl$ChangeName"},
            {"群头衔", "MemberControl$ChangeTitle"},
            {"禁言", "MemberControl$Mute"},
            {"踢", "MemberControl$Kick"},
            {"管理员", "MemberControl$ModifyAdmin"},

            {"退群", "GroupControl$Exit"},
            {"获取群公告", "GroupControl$GetGroupAnnouncement"},
            {"删除群公告", "GroupControl$DelGroupAnnouncement"},
            {"发布群公告", "GroupControl$PublishGroupAnnouncement"},
            {"进群申请处理", "GroupControl$DealMemberJoin"},

            {"群文件", "GroupFileControl$GetFile"},
            {"删群文件", "GroupFileControl$Delete"},
    };

    @Override
    public void onLoad(Object ctx1) {
        Context context = (Context) ctx1;
        DictionaryEnvironment.getInstance().setDicRoot(context.getExternalFilesDir("dic"));
        DictionaryEnvironment.getInstance().setDicConfigPoint(context.getExternalFilesDir("config").toPath().resolve("dicList.json").toFile().getAbsolutePath());
        DictionaryEnvironment.getInstance().setDicData(context.getExternalFilesDir("dicData").toPath());

        Registrator.inject();
        HashMap<String, Class<?>[]> domainQuoteNew = new HashMap<>();
        domainQuoteNew.put("群", new Class[]{GroupMessageEvent.class});
        domainQuoteNew.put("好友", new Class[]{FriendMessageEvent.class});
        domainQuoteNew.put("群事件", new Class[]{GroupMemberEvent.class, MemberJoinRequestEvent.class});
        DictionaryEnvironment.getInstance().getEventDomain().putAll(domainQuoteNew);

        for (String[] dicFunction : dicFunctions) {
            try {
                DictionaryEnvironment.getInstance().getGlobalFunctionRegister()
                        .put(dicFunction[0], (Class<? extends Function>) Class.forName("com.kagg886.seiko.dic.v2.func." + dicFunction[1]));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onBotGoLine(long botQQ) {
        EventChannel<BotEvent> channel = Bot.findInstance(botQQ).getEventChannel();
        boolean refreshDIC = SeikoApplication.globalConfig.getBoolean("alwaysRefreshOnceMessageGetting", false);


        channel.subscribeAlways(GroupMessageEvent.class, event -> { //注册群消息
            if (refreshDIC) {
                List<DICParseResult> res = DICList.INSTANCE.refresh();
                if (res.stream().filter((v) -> !v.success).findFirst().orElse(null) != null) {
                    Objects.requireNonNull(Bot.findInstance(botQQ)).getLogger().info("伪代码解析时遇到问题，请重新刷新词库");
//                    SeikoApplication.getSeikoApplicationContext().
                    return;
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryProject dic : DICList.INSTANCE) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dic.getName());
                Boolean bo = dicConfigUnit.getBoolean("enabled");
                if (bo == null) {
                    bo = true;
                }
                if (!bo) {
                    return;
                }
                GroupMessageRuntime runtime = new GroupMessageRuntime(dic.getIndexFile(), event);
                runtime.invoke(event.getMessage().contentToString());
            }
        });

        channel.subscribeAlways(FriendMessageEvent.class, event -> { //注册好友消息
            if (refreshDIC) {
                List<DICParseResult> res = DICList.INSTANCE.refresh();
                if (res.stream().filter((v) -> !v.success).findFirst().orElse(null) != null) {
                    Objects.requireNonNull(Bot.findInstance(botQQ)).getLogger().info("伪代码解析时遇到问题，请重新刷新词库");
//                    SeikoApplication.getSeikoApplicationContext().
                    return;
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryProject dic : DICList.INSTANCE) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dic.getName());
                Boolean bo = dicConfigUnit.getBoolean("enabled");
                if (bo == null) {
                    bo = true;
                }
                if (!bo) {
                    return;
                }
                FriendMessageRuntime runtime = new FriendMessageRuntime(dic.getIndexFile(), event);
                runtime.invoke(event.getMessage().contentToString());
            }


        });

        channel.subscribeAlways(GroupMemberEvent.class, event -> { //注册成员被踢，主动退群消息 被邀请 主动入群和恢复解散群
            if (refreshDIC) {
                List<DICParseResult> res = DICList.INSTANCE.refresh();
                if (res.stream().filter((v) -> !v.success).findFirst().orElse(null) != null) {
                    Objects.requireNonNull(Bot.findInstance(botQQ)).getLogger().info("伪代码解析时遇到问题，请重新刷新词库");
//                    SeikoApplication.getSeikoApplicationContext().
                    return;
                }
            }

            JSONObject dicConfigUnit;
            for (DictionaryProject dic : DICList.INSTANCE) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dic.getName());
                Boolean bo = dicConfigUnit.getBoolean("enabled");
                if (bo == null) {
                    bo = true;
                }
                if (!bo) {
                    return;
                }
                GroupMemberRuntime runtime = new GroupMemberRuntime(dic.getIndexFile(), event);
                if (event instanceof MemberPermissionChangeEvent) {
                    runtime.getRuntimeObject().put("原权限", ((MemberPermissionChangeEvent) event).getOrigin().toString());
                    runtime.getRuntimeObject().put("原权限代码", ((MemberPermissionChangeEvent) event).getOrigin().getLevel());
                    runtime.getRuntimeObject().put("现权限", ((MemberPermissionChangeEvent) event).getNew().toString());
                    runtime.getRuntimeObject().put("现权限代码", ((MemberPermissionChangeEvent) event).getNew().getLevel());
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
                List<DICParseResult> res = DICList.INSTANCE.refresh();
                if (res.stream().filter((v) -> !v.success).findFirst().orElse(null) != null) {
                    Objects.requireNonNull(Bot.findInstance(botQQ)).getLogger().info("伪代码解析时遇到问题，请重新刷新词库");
//                    SeikoApplication.getSeikoApplicationContext().
                    return;
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryProject dic : DICList.INSTANCE) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dic.getName());
                Boolean bo = dicConfigUnit.getBoolean("enabled");
                if (bo == null) {
                    bo = true;
                }
                if (!bo) {
                    return;
                }
                MemberJoinRequestRuntime runtime = new MemberJoinRequestRuntime(dic.getIndexFile(), event);
                runtime.invoke("申请入群");
            }
        });
    }


    @Override
    public SeikoDescription getDescription() {
        SeikoDescription description = new SeikoDescription(getClass().getName());
        description.setName("Seiko伪代码V2");
        description.setDesc("更强大的DIC引擎");
        description.setAuthor("kagg886");
        description.setVerCode("V2.0.0");
        return description;
    }
}
