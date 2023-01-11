package com.kagg886.seiko.dic;

import android.content.Context;
import android.util.Log;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.util.IOUtil;
import com.kagg886.seiko.dic.util.JSONObjectStorage;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.json.JSONObject;

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
    private static DICList dicLists;
    private static String dicConfigPoint;
    private Context context;

    public static DICList getDicLists() {
        return dicLists;
    }

    public static JSONObjectStorage getDicConfig() {
        return JSONObjectStorage.obtain(dicConfigPoint);
    }

    @Override
    public void onBotGoLine(long botQQ) {
        EventChannel<BotEvent> event = Bot.findInstance(botQQ).getEventChannel();
        event.subscribeAlways(GroupMessageEvent.class, new Consumer<GroupMessageEvent>() {
            @Override
            public void accept(GroupMessageEvent groupMessageEvent) {
                JSONObject dicConfigUnit;
                for (DictionaryFile dic : dicLists) {
                    if ((dicConfigUnit = getDicConfig().optJSONObject(dic.getName(), new JSONObject())) != null) {
                        if (dicConfigUnit.optBoolean("enabled", true)) {
                            dic.invoke(groupMessageEvent);
                            Log.i("DEBUG", dic.getName() + "已执行");
                            return;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBotOffLine(long botQQ) {

    }

    @Override
    public void onLoad(Object ctx) {
        this.context = (Context) ctx;
        dicLists = new DICList(context);
        dicConfigPoint = IOUtil.newFile(context.getExternalFilesDir("config"), "/dicList.json").getAbsolutePath();
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