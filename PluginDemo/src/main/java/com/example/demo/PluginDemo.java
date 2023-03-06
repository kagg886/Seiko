package com.example.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.GroupMessageEvent;

/**
 * @projectName: Seiko
 * @package: com.example.demo
 * @className: PluginDemo
 * @author: kagg886
 * @description: 插件Demo
 * @date: 2023/1/12 11:48
 * @version: 1.0
 */
public class PluginDemo extends SeikoPlugin {

    /*
     * @param botQQ:
     * @return void
     * @author kagg886
     * @description 每当一个bot上线成功就调用此方法
     * @date 2023/01/12 11:58
     */
    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onBotGoLine(long botQQ) {
        Log.i("LIFECYCLE", "onBotGoLine被调用");
        Bot.findInstance(botQQ).getEventChannel().subscribeAlways(GroupMessageEvent.class, groupMessageEvent -> {
            //在此处编写自定义逻辑
        });
    }

    /*
     * @param context:
     * @return void
     * @author kagg886
     * @description 插件被加载时调用。
     * @date 2023/01/12 11:57
     */
    @Override
    public void onLoad(Object context) {
        Log.i("LIFECYCLE", "onLoad被调用");
        Toast.makeText((Context) context, getClass().getName() + "已加载!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnLoad() {
        Log.i("LIFECYCLE", "onUnLoad被调用");
    }

    /*
     * @param :
     * @return SeikoDescription
     * @author kagg886
     * @description 向SeikoAPP回传描述。请不要在这里调用任何初始化的方法
     * @date 2023/01/12 11:57
     */
    @Override
    public SeikoDescription getDescription() {
        Log.i("LIFECYCLE", "getDescription被调用");
        SeikoDescription description = new SeikoDescription(getClass().getName());
        description.setName("PluginDemo");
        description.setAuthor("佚名");
        description.setVerCode("1.0");
        description.setDesc("一个用于测试的样板而已");
        return description;
    }
}
