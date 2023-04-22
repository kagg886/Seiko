package com.kagg886.seiko.dic.session.impl;

import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: LifeCycleRuntime
 * @author: kagg886
 * @description: 生命周期函数,每个词库都不必拥有
 * @date: 2023/4/12 8:26
 * @version: 1.0
 */
public class LifeCycleRuntime extends AbsRuntime<DictionaryFile> {

    public enum LifeCycle {
        INIT("初始化"),
        DESTROY("被销毁");

        private final String tips;

        LifeCycle(String s) {
            this.tips = s;
        }

        public String getTips() {
            return tips;
        }

    }

    public LifeCycleRuntime(DictionaryFile file) {
        super(file,file);
    }

    @Override
    protected Contact initContact(DictionaryFile EVENT) {
        return null;
    }

    @Override
    public void clearMessage() {
        for (Bot b : Bot.getInstances()) {
            b.getLogger().info(String.format("[%s]:%s",event.getName(),getMessageCache().build().contentToString()));
            if (!DictionaryEnvironment.getInstance().isShowLogOnAllBots()) {
                break;
            }
        }
        context.put("缓冲区", new MessageChainBuilder());
    }
}
