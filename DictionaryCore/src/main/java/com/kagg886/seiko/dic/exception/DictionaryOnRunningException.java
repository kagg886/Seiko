package com.kagg886.seiko.dic.exception;

import com.kagg886.seiko.dic.entity.DictionaryFile;

import java.util.Stack;

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
        super(String.format("词库运行出错!\n文件:%s\n原因:%s\n词库栈:%s\nJava栈", file.getFile().getAbsolutePath(), msg, stack.toString()));
        setStackTrace(cause.getStackTrace());
        this.msg = msg;
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
        return msg;
    }
}
