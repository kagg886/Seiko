package com.kagg886.seiko.dic;

import android.content.Context;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.dic.bridge.DictionaryListener;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.model.DICParseResult;
import com.kagg886.seiko.dic.session.impl.FriendMessageRuntime;
import com.kagg886.seiko.dic.session.impl.GroupMemberRuntime;
import com.kagg886.seiko.dic.session.impl.GroupMessageRuntime;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.util.IOUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import org.json.JSONObject;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic
 * @className: DICPlugin
 * @author: kagg886
 * @description: SeikoDIC插件入口
 * @date: 2023/1/9 17:31
 * @version: 1.0
 */
public class DICPlugin extends SeikoPlugin implements DictionaryListener {

    private Context context;

    @Override
    public void onBotGoLine(long botQQ) {
        EventChannel<BotEvent> event = Bot.findInstance(botQQ).getEventChannel();

        event.subscribeAlways(GroupMemberEvent.class, e -> {
            if (SeikoApplication.globalConfig.getBoolean("alwaysRefreshOnceMessageGetting", false)) {
                DICParseResult result = DICList.getInstance().refresh();
                if(!result.success) {
                    SnackBroadCast.sendBroadCast("伪代码解析中存在问题！请检查无法被启用的伪代码，出现的问题：" + result.err);
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.getInstance()) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(dic.getName());
                if (dicConfigUnit.optBoolean("enabled", true)) {
                    GroupMemberRuntime runtime = new GroupMemberRuntime(dic, e);
                    if (e instanceof MemberLeaveEvent.Kick) {
                        runtime.invoke("成员被踢");
                        return;
                    }

                    if (e instanceof MemberLeaveEvent.Quit) {
                        runtime.invoke("成员主动退群");
                        return;
                    }
                }
            }
        });


        event.subscribeAlways(GroupMessageEvent.class, groupMessageEvent -> {
            if (SeikoApplication.globalConfig.getBoolean("alwaysRefreshOnceMessageGetting", false)) {
                DICParseResult result = DICList.getInstance().refresh();
                if(!result.success) {
                    SnackBroadCast.sendBroadCast("伪代码解析中存在问题！请检查无法被启用的伪代码，出现的问题：" + result.err);
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.getInstance()) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(dic.getName());
                if (dicConfigUnit == null || dicConfigUnit.optBoolean("enabled", true)) {
                    GroupMessageRuntime runtime = new GroupMessageRuntime(dic, groupMessageEvent);
                    runtime.invoke(groupMessageEvent.getMessage().contentToString());
                }
            }
        });

        event.subscribeAlways(FriendMessageEvent.class, friendMessageEvent -> {
            if (SeikoApplication.globalConfig.getBoolean("alwaysRefreshOnceMessageGetting", false)) {
                DICParseResult result = DICList.getInstance().refresh();
                if(!result.success) {
                    SnackBroadCast.sendBroadCast("伪代码解析中存在问题！请检查无法被启用的伪代码，出现的问题：" + result.err);
                }
            }
            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.getInstance()) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(dic.getName());
                if (dicConfigUnit == null || dicConfigUnit.optBoolean("enabled", true)) {
                    FriendMessageRuntime runtime = new FriendMessageRuntime(dic, friendMessageEvent);
                    runtime.invoke(friendMessageEvent.getMessage().contentToString());
                }
            }
        });
    }

    @Override
    public void onLoad(Object ctx) {
        this.context = (Context) ctx;
        DictionaryEnvironment.getInstance().setDicRoot(context.getExternalFilesDir("dic"));
        DictionaryEnvironment.getInstance().setErrorListener(this);
        DictionaryEnvironment.getInstance().setDicConfigPoint(context.getExternalFilesDir("config").toPath().resolve("dicList.json").toFile().getAbsolutePath());
        DictionaryEnvironment.getInstance().setDicData(context.getExternalFilesDir("dicData").toPath());
    }

    @Override
    public SeikoDescription getDescription() {
        SeikoDescription description = new SeikoDescription(getClass().getName());
        description.setName("Seiko伪代码");
        description.setDesc("简易DIC插件");
        description.setAuthor("kagg886");
        description.setVerCode("V0.0.1");
        return description;
    }

    @Override
    public void onWarn(File p, String message) {
        DialogBroadCast.sendBroadCast("加载伪代码文件:[" + p.getName() + "]发现一个潜在性问题:", message);
    }
}
