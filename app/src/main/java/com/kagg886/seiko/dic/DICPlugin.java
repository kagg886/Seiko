package com.kagg886.seiko.dic;

import android.content.Context;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import io.github.seikodictionaryenginev2.base.env.DictionaryEnvironment;
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
public class DICPlugin extends SeikoPlugin {

    @Override
    public void onBotGoLine(long botQQ) {
//        EventChannel<BotEvent> channel = Bot.findInstance(botQQ).getEventChannel();
//
//        DictionaryReg.CallBack callBack = (logger, result) -> {
//            logger.warning("伪代码解析中存在问题！请检查无法被启用的伪代码");
//            result.err.forEach(logger::warning);
//        };
//
//        DictionaryReg.reg(channel, SeikoApplication.globalConfig.getBoolean("alwaysRefreshOnceMessageGetting", false),
//                callBack);
    }

    @Override
    public void onLoad(Object ctx1) {
        Context context = (Context) ctx1;
        DictionaryEnvironment.getInstance().setDicRoot(context.getExternalFilesDir("dic"));
        DictionaryEnvironment.getInstance().setDicConfigPoint(context.getExternalFilesDir("config").toPath().resolve("dicList.json").toFile().getAbsolutePath());
        DictionaryEnvironment.getInstance().setDicData(context.getExternalFilesDir("dicData").toPath());

//        if (SeikoApplication.globalConfig.getBoolean("mergeAllLogs", false)) {
//            DictionaryEnvironment.getInstance().setShowLogOnAllBots(false);
//        }
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
