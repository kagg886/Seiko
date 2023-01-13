package com.kagg886.seiko.dic.entity;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity
 * @className: DictionaryCommand
 * @author: kagg886
 * @description: 代表了一行伪代码
 * @date: 2023/1/11 15:25
 * @version: 1.0
 */
public class DictionaryCode {
    private final int line;
    private final String code;

    public DictionaryCode(int line, String code) {
        this.line = line;
        this.code = code;
    }
}
