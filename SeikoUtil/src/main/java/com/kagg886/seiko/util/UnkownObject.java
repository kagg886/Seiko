package com.kagg886.seiko.util;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.util
 * @className: UnkownObject
 * @author: kagg886
 * @description: 代表了一个未知的变量
 * @date: 2023/1/20 20:20
 * @version: 1.0
 */
public class UnkownObject {
    private final Object arg;

    public UnkownObject(Object arg) {
        this.arg = arg;
    }

    public Object getObject() {
        return arg;
    }
}
