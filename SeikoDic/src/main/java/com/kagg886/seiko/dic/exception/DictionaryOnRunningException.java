package com.kagg886.seiko.dic.exception;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.exception
 * @className: DictionaryOnRunningException
 * @author: kagg886
 * @description: 词库在运行时抛出异常
 * @date: 2023/1/18 11:17
 * @version: 1.0
 */
public class DictionaryOnRunningException extends RuntimeException {

    public DictionaryOnRunningException(String s) {
        super(s);
    }

    public DictionaryOnRunningException(String s, Throwable e) {
        super(s, e);
    }
}
