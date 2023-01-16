package com.kagg886.seiko.dic.session;

import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.DictionaryCommandMatcher;
import com.kagg886.seiko.dic.entity.DictionaryFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.session
 * @className: AbsRuntime
 * @author: kagg886
 * @description: 代表一次处理。插件收到事件后，会匹配符合正则表达式的条目
 * @date: 2023/1/12 21:21
 * @version: 1.0
 */
public abstract class AbsRuntime<T> {
    protected final T event; //此次执行伪代码所需要的事件
    protected DictionaryFile file; //被执行的伪代码指令集
    protected HashMap<String, Object> context; //此次伪代码执行过程中存取的变量

    public AbsRuntime(DictionaryFile file, T event) {
        this.file = file;
        this.event = event;
        context = new HashMap<>();
    }

    public HashMap<String, Object> getRuntimeObject() {
        return context;
    }

    public void invoke(String command) {
        for (Map.Entry<DictionaryCommandMatcher, ArrayList<DictionaryCode>> entry : file.getCommands().entrySet()) {
            DictionaryCommandMatcher matcher = entry.getKey();
            ArrayList<DictionaryCode> code = entry.getValue();
            if (!matcher.matchesDomain(event)) { //匹配指令触发的环境和当前环境是否相符
                return;
            }
            if (matcher.matchesCommand(command)) { //正则匹配
                invoke(code);
            }
        }
    }

    protected abstract void invoke(ArrayList<DictionaryCode> code);


}
