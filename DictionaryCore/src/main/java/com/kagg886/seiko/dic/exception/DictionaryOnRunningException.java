package com.kagg886.seiko.dic.exception;

import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.util.TextUtils;

import java.util.*;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.exception
 * @className: DictionaryOnRunningException
 * @author: kagg886
 * @description: 伪代码在运行时抛出异常
 * @date: 2023/1/18 11:17
 * @version: 1.0
 */
public class DictionaryOnRunningException extends RuntimeException {

    private String msg;

    public DictionaryOnRunningException(DictionaryFile file, String msg, AbsRuntime<?> runtime, Stack<String> stack, Exception cause) {
        super(String.format("词库运行出错!\n文件:%s\n原因:%s\n词库栈:%s\n变量集:%s\nJava栈",
                file.getFile().getAbsolutePath(),
                msg, stackToString(stack),
                mapToString(runtime.getRuntimeObject())),cause);
        setStackTrace(cause.getStackTrace());
        this.msg = msg;
    }

    public static String mapToString(Map<String, Object> obj) {
        return mapToString(obj, 0, new StringBuilder());
    }

    private static String mapToString(Object objs, int deep, StringBuilder builder) {
        builder.append("\n");
        if (objs instanceof Map<?, ?>) {
            Map<String, Object> obj = ((Map<String, Object>) objs);
            for (Map.Entry<String, Object> entry : obj.entrySet()) {
                builder.append(TextUtils.repeat("  ",Math.max(0, deep)));
                builder.append(entry.getKey());
                builder.append("---");
                Object value = entry.getValue();
                mapToString0(deep, builder, value);
            }
        } else if (objs instanceof List<?>) {
            List<Object> obj = (List<Object>) objs;
            for (Object value : obj) {
                builder.append(TextUtils.repeat("  ",Math.max(0, deep)));
                mapToString0(deep, builder, value);
            }
        }
        return builder.substring(1, builder.length() - 1);
    }

    private static void mapToString0(int deep, StringBuilder builder, Object value) {
        if (value == null) {
            builder.append("null");
        } else if (value instanceof Map<?, ?>) {
            builder.append("{");
            mapToString(value, deep + 1, builder);
            builder.append(TextUtils.repeat("  ",Math.max(0, deep)));
            builder.append("}");
        } else if (value instanceof List<?>) {
            builder.append("[");
            mapToString(value, deep + 1, builder);
            builder.append(TextUtils.repeat("  ",Math.max(0, deep)));
            builder.append("]");
        } else {
            builder.append(value);
            builder.append(",");
        }
        builder.append("\n");
    }

    public static String stackToString(Stack<String> stack) {
        StringBuilder builder = new StringBuilder();
        stack.stream().sorted(Comparator.reverseOrder()).forEach(s -> {
            builder.append("\n");
            builder.append(s);
        });
        return builder.substring(1);
    }

    public DictionaryOnRunningException(Throwable e) {
        super(e);
    }

    public DictionaryOnRunningException(String s) {
        super(s);
    }

    public DictionaryOnRunningException(String s, Throwable e) {
        super(s, e);
    }

    public String getMsg() {
        return msg == null ? getMessage() : msg;
    }
}
