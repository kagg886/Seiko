package com.kagg886.seiko.dic;

import android.content.Context;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.dic.bridge.DictionaryListener;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;

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
        EventChannel<BotEvent> channel = Bot.findInstance(botQQ).getEventChannel();

        DictionaryReg.CallBack callBack = (logger, result) -> {
            logger.warning("伪代码解析中存在问题！请检查无法被启用的伪代码");
            result.err.forEach(logger::warning);
        };

        DictionaryReg.reg(channel, SeikoApplication.globalConfig.getBoolean("alwaysRefreshOnceMessageGetting", false),
                callBack);
    }

    @Override
    public void onLoad(Object ctx) {
        this.context = (Context) ctx;
        DictionaryEnvironment.getInstance().setDicRoot(context.getExternalFilesDir("dic"));
        DictionaryEnvironment.getInstance().setErrorListener(this);
        DictionaryEnvironment.getInstance().setDicConfigPoint(context.getExternalFilesDir("config").toPath().resolve("dicList.json").toFile().getAbsolutePath());
        DictionaryEnvironment.getInstance().setDicData(context.getExternalFilesDir("dicData").toPath());

        if (SeikoApplication.globalConfig.getBoolean("mergeAllLogs", false)) {
            DictionaryEnvironment.getInstance().setShowLogOnAllBots(false);
        }
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
