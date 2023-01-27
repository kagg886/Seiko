package com.kagg886.seiko.util;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.util
 * @className: TextUtils
 * @author: kagg886
 * @description: 文本检查类
 * @date: 2023/1/27 16:05
 * @version: 1.0
 */
public class TextUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
