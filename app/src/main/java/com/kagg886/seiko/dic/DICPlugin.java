package com.kagg886.seiko.dic;

import android.content.Context;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.dic.bridge.DictionaryListener;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.impl.FriendMessageRuntime;
import com.kagg886.seiko.dic.session.impl.GroupMessageRuntime;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.util.IOUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
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

        event.subscribeAlways(GroupMessageEvent.class, groupMessageEvent -> {
            if (SeikoApplication.globalConfig.getBoolean("alwaysRefreshOnceMessageGetting", false)) {
                DICList.getInstance().refresh();
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
                DICList.getInstance().refresh();
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
    public void onError(File p, Throwable e) {
        if (SeikoApplication.globalConfig.getBoolean("badDicAutoDel", false)) {
            p.delete();
        }
        DialogBroadCast.sendBroadCast(context, "加载伪代码文件:[" + p.getName() + "]时发生错误!", IOUtil.getException(e));
    }

    @Override
    public void onWarn(File p, String message) {
        DialogBroadCast.sendBroadCast(context, "加载伪代码文件:[" + p.getName() + "]发现一个潜在性问题:", message);
    }
}
