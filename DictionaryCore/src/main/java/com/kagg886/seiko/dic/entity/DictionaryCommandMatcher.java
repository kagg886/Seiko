package com.kagg886.seiko.dic.entity;

import com.kagg886.seiko.dic.exception.DictionaryOnLoadException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMemberEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity
 * @className: DictionaryCommandMatcher
 * @author: kagg886
 * @description: 指令匹配器，由正则表达式和适用的事件类型组成
 * @date: 2023/1/12 21:35
 * @version: 1.0
 */
public class DictionaryCommandMatcher {
    private static final String[][] domainQuote = { //允许的事件类型
            {"群", GroupMessageEvent.class.getName()},
            {"好友", FriendMessageEvent.class.getName()},
            {"函数", AbsRuntime.class.getName()}, //这个触发事件是由runTime调用
            {"群事件", GroupMemberEvent.class.getName()},
            {"生命周期",DictionaryFile.class.getName()}
    };
    private final Pattern pattern;
    private final String source;
    private final int line;
    private final String[] eventClassNames;

    public DictionaryCommandMatcher(String commandRegex, int line, File dicFile) {
        this.source = commandRegex;
        this.line = line;
        int p = commandRegex.indexOf("]");
        if (p == -1 || commandRegex.lastIndexOf("[", p) == -1) {
            throw new DictionaryOnLoadException("方法没有标记事件类型:(" + dicFile.getAbsolutePath() + ":" + line + ")");
        }
        eventClassNames = commandRegex.substring(1, p).split("\\|");
        pattern = Pattern.compile(commandRegex.substring(p + 1));

        int matches = 0;
        for (int i = 0; i < eventClassNames.length; i++) {
            for (String[] a : domainQuote) {
                if (eventClassNames[i].equals(a[0])) {
                    eventClassNames[i] = a[1];
                    matches++;
                    break;
                }
            }
        }
        if (matches != eventClassNames.length) {
            throw new DictionaryOnLoadException("加载过程中发现未定义的事件类型:" + "(" + dicFile.getAbsolutePath() + ":" + line + ")");
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DictionaryCommandMatcher && source.equals(obj.toString());
    }

    @NotNull
    @Override
    public String toString() {
        return source;
    }

    public int getLine() {
        return line;
    }

    public boolean matchesCommand(String regex) {
        Matcher matcher = pattern.matcher(regex);
        return matcher.matches();
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean matchesDomain(Object o) {
        for (String className : eventClassNames) {
            Class clazz = o.getClass();
            do {
                if (clazz.getName().equals(className)) {
                    return true;
                }
                for (Class interfaces : clazz.getInterfaces()) {
                    if (interfaces.getName().equals(className)) {
                        return true;
                    }
                }
            } while ((clazz = clazz.getSuperclass()) != Object.class);
        }

        return false;
    }
}
