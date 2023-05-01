package com.kagg886.seiko.dic.session.impl;

import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.HashMap;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session.impl
 * @className: FunctionRuntime
 * @author: kagg886
 * @description: 伪代码托管执行器。它没有自己的上下文
 * @date: 2023/2/17 10:34
 * @version: 1.0
 */
public class FunctionRuntime extends AbsRuntime<AbsRuntime<?>> {

    private Class<?> proxyClass = super.getProxyClass();

    public FunctionRuntime(DictionaryFile file, AbsRuntime<?> event) {
        super(file, event);
        this.exceptionStacks = event.getExceptionStacks(); //继承原调用栈
        this.exceptionCaller = event.getExceptionCaller();
        this.exceptionCaller_name = event.getExceptionCaller_name();
    }

    @Override
    public Class<?> getProxyClass() {
        return proxyClass;
    }


    public void setProxyClass(Class<?> proxyClass) {
        this.proxyClass = proxyClass;
    }

    @Override
    public void clearMessage() {
        event.clearMessage();
    }

    @Override
    public void setContact(Contact contact) {
        event.setContact(contact);
    }

    @Override
    public Contact getContact() {
        return event.getContact();
    }

    @Override
    protected Contact initContact(AbsRuntime<?> absRuntime) {
        return absRuntime.getContact();
    }

    @Override
    public HashMap<String, Object> getRuntimeObject() {
        return event.getRuntimeObject();
    }

    @Override
    public MessageChainBuilder getMessageCache() {
        return event.getMessageCache();
    }
}
