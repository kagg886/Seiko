package com.kagg886.seiko.dic.entity;

import com.kagg886.seiko.dic.exception.DictionaryOnLoadException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMemberEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private static final Map<String,Class<?>[]> domainQuoteNew; //记录了所有的合法标签
    private final Pattern pattern;//匹配字符串
    private final String source; //源表达式
    private final int line;//行数
    private final List<Class<?>> eventClass = new ArrayList<>(); //根据事件类型注册的Class

    static {
        domainQuoteNew = new HashMap<>();
        domainQuoteNew.put("群", new Class[]{GroupMessageEvent.class});
        domainQuoteNew.put("好友", new Class[]{FriendMessageEvent.class});
        domainQuoteNew.put("函数", new Class[]{AbsRuntime.class, DictionaryFile.class});
        domainQuoteNew.put("群事件", new Class[]{GroupMemberEvent.class, MemberJoinRequestEvent.class});
    }

    public DictionaryCommandMatcher(String commandRegex, int line, File dicFile) {
        this.source = commandRegex;
        this.line = line;
        int p = commandRegex.indexOf("]");
        if (p == -1 || commandRegex.lastIndexOf("[", p) == -1) {
            throw new DictionaryOnLoadException("方法没有标记事件类型:(" + dicFile.getAbsolutePath() + ":" + line + ")");
        }
        String[] eventClassNames = commandRegex.substring(1, p).split("\\|");
        pattern = Pattern.compile(commandRegex.substring(p + 1));

        int matches = 0;
        for (String eventClassName : eventClassNames) {
            for (Map.Entry<String, Class<?>[]> ent : domainQuoteNew.entrySet()) {
                if (eventClassName.equals(ent.getKey())) {
                    //eventClass.addAll(List.of(ent.getValue()));
                    eventClass.addAll(Arrays.stream(ent.getValue()).collect(Collectors.toList()));
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

    public boolean matchesDomain(AbsRuntime<?> o) {
        for (Class<?> a : eventClass) {
            Class<?> clazz = o.getProxyClass();
            do {
                if (clazz == a) {
                    return true;
                }
                for (Class<?> interfaces : clazz.getInterfaces()) { //爬接口
                    if (interfaces == a) {
                        return true;
                    }
                }
            } while ((clazz = clazz.getSuperclass()) != Object.class); //爬父类
        }

        return false;
    }
}
