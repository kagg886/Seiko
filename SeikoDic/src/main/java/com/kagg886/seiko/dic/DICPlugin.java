package com.kagg886.seiko.dic;

import android.content.Context;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.GroupEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.function.Consumer;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic
 * @className: DICPlugin
 * @author: kagg886
 * @description: SeikoDIC插件入口
 * @date: 2023/1/9 17:31
 * @version: 1.0
 */
public class DICPlugin implements SeikoPlugin {
    private Context context;
    private static DICList dicLists;

    public static DICList getDicLists() {
        return dicLists;
    }

    @Override
    public void onBotGoLine(long botQQ) {
        EventChannel<BotEvent> event = Bot.findInstance(botQQ).getEventChannel();
        event.subscribeAlways(GroupMessageEvent.class,new Consumer<GroupMessageEvent>() {
            @Override
            public void accept(GroupMessageEvent groupMessageEvent) {
                //TODO 在此处构建消息包并发送到词库引擎
            }
        });
    }

    @Override
    public void onBotOffLine(long botQQ) {

    }

    @Override
    public void onLoad(Context context) {
        this.context = context;
        dicLists = new DICList(context);
    }

    @Override
    public SeikoDescription getDescription() {
        SeikoDescription description = new SeikoDescription(getClass().getName());
        description.setName("Seiko词库");
        description.setDesc("简易DIC插件");
        description.setAuthor("kagg886");
        description.setVerCode(BuildConfig.VERSION);
        return description;
    }
}
