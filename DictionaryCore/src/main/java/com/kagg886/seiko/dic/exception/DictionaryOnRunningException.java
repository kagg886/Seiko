package com.kagg886.seiko.dic.exception;

import com.kagg886.seiko.dic.entity.DictionaryFile;

import java.util.Comparator;
import java.util.Stack;
import java.util.stream.Collectors;

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

    public DictionaryOnRunningException(DictionaryFile file, String msg, Stack<String> stack, Exception cause) {
        super(String.format("词库运行出错!\n文件:%s\n原因:%s\n词库栈:%s\nJava栈", file.getFile().getAbsolutePath(), msg, stackToString(stack)));
        setStackTrace(cause.getStackTrace());
        this.msg = msg;
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
