package com.kagg886.seiko.dic.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.kagg886.seiko.dic.exception.DictionaryOnLoadException;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
            {"好友", FriendMessageEvent.class.getName()}
    };
    private final Pattern pattern;
    private final String source;
    private final int line;
    private String eventClassName;

    public DictionaryCommandMatcher(String commandRegex, int line, File dicFile) {
        this.source = commandRegex;
        this.line = line;
        int p = commandRegex.indexOf("]");
        if (p == -1 || commandRegex.lastIndexOf("[", p) == -1) {
            throw new DictionaryOnLoadException("方法没有标记事件类型:(" + dicFile.getAbsolutePath() + ":" + line + ")");
        }
        eventClassName = commandRegex.substring(1, p);
        for (String[] a : domainQuote) {
            if (eventClassName.equals(a[0])) {
                eventClassName = a[1];
                pattern = Pattern.compile(commandRegex.substring(p + 1));
                return;
            }
        }
        throw new DictionaryOnLoadException("加载过程中发现未定义的事件类型:" + eventClassName + "(" + dicFile.getAbsolutePath() + ":" + line + ")");
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        return source.equals(obj.toString());
    }

    @NonNull
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

    public boolean matchesDomain(Object o) {
        return o.getClass().getName().equals(eventClassName);
    }
}
