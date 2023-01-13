package com.kagg886.seiko.dic.session.impl;

import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.entity.impl.Function;
import com.kagg886.seiko.dic.entity.impl.PlainText;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: GroupMessageRuntime
 * @author: kagg886
 * @description: 群消息
 * @date: 2023/1/12 21:26
 * @version: 1.0
 */
public class GroupMessageRuntime extends AbsRuntime<GroupMessageEvent> {

    public GroupMessageRuntime(DictionaryFile file, GroupMessageEvent event) {
        super(file, event);
        context.put("上下文", event);
        context.put("群号", event.getGroup().getId());
        context.put("群名称", event.getGroup().getName());
        context.put("QQ", event.getSender().getId());
        context.put("昵称", event.getSender().getNick());
        context.put("群名片", event.getSender().getId());
        context.put("BOT", event.getBot().getId());
    }

    /*
     * @param code:
     * @return void
     * @author kagg886
     * @description 词库方法的解析需要有三步:变量替换成常量，计算表达式，调用
     * @date 2023/01/13 09:41
     */
    @Override
    protected void invoke(ArrayList<DictionaryCode> code) {
        boolean needSend = !(code.get(0) instanceof PlainText); //若第一行为PlainText返回false。为Function返回true

        StringBuilder buffer = new StringBuilder();
        for (DictionaryCode dic : code) {
            if (dic instanceof Function) {
                if (!needSend) {
                    event.getGroup().sendMessage(buffer.toString());
                    buffer = new StringBuilder();
                    needSend = true;
                }
                //TODO 处理函数
            }

            if (dic instanceof PlainText) {
                buffer.append(dic.cleanVariableCode(this));
                needSend = false;
            }
        }
    }
}
