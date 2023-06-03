package com.kagg886.seiko.util;

import java.util.regex.Pattern;

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

    private static final Pattern p = Pattern.compile("^[A-Fa-f0-9]+$");

    public static String repeat(String value, int count) {
        if (count == 1) {
            return value;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }

    public static boolean checkHex(String a) {
        a = a.replace(" ", "");
        return p.matcher(a).matches();
    }
}
