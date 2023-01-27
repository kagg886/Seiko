package com.kagg886.seiko.dic.mirai_console;

import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.impl.FriendMessageRuntime;
import com.kagg886.seiko.dic.session.impl.GroupMessageRuntime;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.json.JSONObject;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.mirai_console
 * @className: MiraiMessageHandler
 * @author: kagg886
 * @description: 消息接收类
 * @date: 2023/1/27 17:43
 * @version: 1.0
 */
public class MiraiMessageHandler extends SimpleListenerHost {

    @EventHandler
    public void onMessage(GroupMessageEvent groupMessageEvent) {
        JSONObject dicConfigUnit;
        for (DictionaryFile dic : DICList.getInstance()) {
            dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(dic.getName(), new JSONObject());
            if (dicConfigUnit.optBoolean("enabled", true)) {
                GroupMessageRuntime runtime = new GroupMessageRuntime(dic, groupMessageEvent);
                runtime.invoke(groupMessageEvent.getMessage().contentToString());
            }
        }
    }

    @EventHandler
    public void onMessage(FriendMessageEvent friendMessageEvent) {
        JSONObject dicConfigUnit;
        for (DictionaryFile dic : DICList.getInstance()) {
            if ((dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(dic.getName(), new JSONObject())) != null) {
                if (dicConfigUnit.optBoolean("enabled", true)) {
                    FriendMessageRuntime runtime = new FriendMessageRuntime(dic, friendMessageEvent);
                    runtime.invoke(friendMessageEvent.getMessage().contentToString());
                }
            }
        }
    }
}
