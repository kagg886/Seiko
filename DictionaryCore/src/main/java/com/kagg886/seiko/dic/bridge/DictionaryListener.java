package com.kagg886.seiko.dic.bridge;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.Interface
 * @className: ErrorListener
 * @author: kagg886
 * @description: DIC的总监听器
 * @date: 2023/1/27 15:38
 * @version: 1.0
 */
public interface DictionaryListener {
    void onError(File p, Throwable e);

    void onWarn(File p, String message);
}
