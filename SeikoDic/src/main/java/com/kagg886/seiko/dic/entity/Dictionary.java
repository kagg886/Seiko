package com.kagg886.seiko.dic.entity;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity
 * @className: Dictionary
 * @author: kagg886
 * @description: 代表了一个伪代码实例
 * @date: 2023/1/9 19:34
 * @version: 1.0
 */
public class Dictionary {
    private File dicFile;

    public Dictionary(File dicFile) {
        this.dicFile = dicFile;
        //TODO 在这里进行DIC的解析
    }

    public String getName() {
        return dicFile.getName();
    }
}
