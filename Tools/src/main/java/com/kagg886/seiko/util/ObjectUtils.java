package com.kagg886.seiko.util;

/**
 * @author kagg886
 * @date 2023/6/2 19:30
 **/
public class ObjectUtils {
    public static void requireNull(Object... obj) {
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] == null) {
                throw new NullPointerException("arg[" + i + "] is null!");
            }
        }
    }
}
