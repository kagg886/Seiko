package com.kagg886.seiko.util;

import java.util.ArrayList;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.util
 * @className: LimitedArrayList
 * @author: kagg886
 * @description: 带有限制的ArrayList，当超限时则移除头元素
 * @date: 2023/1/10 12:05
 * @version: 1.0
 */
public class LimitedArrayList<T> extends ArrayList<T> {
    private final int len;

    public LimitedArrayList(int len) {
        this.len = len;
    }

    @Override
    public boolean add(T t) {
        if (size() > len) {
            remove(0);
        }
        return super.add(t);
    }
}
