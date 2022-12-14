package com.example.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.function.Consumer;

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
        Bot.findInstance(botQQ).getEventChannel().subscribeAlways(GroupMessageEvent.class, new Consumer<GroupMessageEvent>() {
            @Override
            public void accept(GroupMessageEvent groupMessageEvent) {
                //在此处编写自定义逻辑
            }
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
        Toast.makeText((Context) context, getClass().getName() + "已加载!", Toast.LENGTH_SHORT).show();
    }

    /*
     * @param :
     * @return SeikoDescription
     * @author kagg886
     * @description 向SeikoAPP回传描述文件
     * @date 2023/01/12 11:57
     */
    @Override
    public SeikoDescription getDescription() {
        SeikoDescription description = new SeikoDescription(getClass().getName());
        description.setName("PluginDemo");
        description.setAuthor("佚名");
        description.setVerCode("1.0");
        description.setDesc("一个用于测试的样板而已");
        return description;
    }
}
