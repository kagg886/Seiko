package com.kagg886.seiko.dic.exception;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.exception
 * @className: DictionaryFileEmptyException
 * @author: kagg886
 * @description: 加载词库出错时抛出
 * @date: 2023/1/11 15:19
 * @version: 1.0
 */
public class DictionaryOnLoadException extends RuntimeException {
    public DictionaryOnLoadException(String ss) {
        super(ss);
    }
}
