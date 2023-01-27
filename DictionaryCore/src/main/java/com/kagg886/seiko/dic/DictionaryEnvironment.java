package com.kagg886.seiko.dic;

import com.kagg886.seiko.dic.Interface.ErrorListener;
import com.kagg886.seiko.util.storage.JSONObjectStorage;

import java.io.File;
import java.nio.file.Path;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic
 * @className: DictionaryEnvironment
 * @author: kagg886
 * @description: Dictionary所处的环境类
 * @date: 2023/1/27 15:24
 * @version: 1.0
 */
public class DictionaryEnvironment {
    private static final DictionaryEnvironment DICTIONARY_ENVIRONMENT = new DictionaryEnvironment();

    private String dicConfigPoint; //dicConfig存储路径

    private Path dicData; //dic生成的文件跟路径


    private File dicRoot; //dic存储的根目录

    private ErrorListener errorListener; //DIC刷新监听器

    private DictionaryEnvironment() {

    }

    public static DictionaryEnvironment getInstance() {
        return DICTIONARY_ENVIRONMENT;
    }

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public File getDicRoot() {
        return dicRoot;
    }

    public void setDicRoot(File dicRoot) {
        this.dicRoot = dicRoot;
    }

    public JSONObjectStorage getDicConfig() {
        return JSONObjectStorage.obtain(dicConfigPoint);
    }

    public Path getDicData() {
        return dicData;
    }

    public void setDicData(Path dicData) {
        this.dicData = dicData;
    }

    public void setDicConfigPoint(String dicConfigPoint) {
        this.dicConfigPoint = dicConfigPoint;
    }
}
