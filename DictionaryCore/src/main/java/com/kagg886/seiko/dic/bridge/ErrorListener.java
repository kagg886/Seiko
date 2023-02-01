package com.kagg886.seiko.dic.bridge;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.Interface
 * @className: ErrorListener
 * @author: kagg886
 * @description: DIC被刷新时调用此方法
 * @date: 2023/1/27 15:38
 * @version: 1.0
 */
public interface ErrorListener {
    void onError(File p, Throwable e);
}
