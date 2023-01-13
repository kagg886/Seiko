package com.kagg886.seiko.dic.session.impl;

import android.util.Log;
import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.event.events.FriendMessageEvent;

import java.util.ArrayList;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: FriendMessageRuntime
 * @author: kagg886
 * @description: 好友运行时
 * @date: 2023/1/12 22:22
 * @version: 1.0
 */
public class FriendMessageRuntime extends AbsRuntime<FriendMessageEvent> {
    public FriendMessageRuntime(DictionaryFile file, FriendMessageEvent event) {
        super(file, event);
    }

    @Override
    protected void invoke(ArrayList<DictionaryCode> code) {
        Log.i("DEBUG", "好友方法触发!");
    }
}
