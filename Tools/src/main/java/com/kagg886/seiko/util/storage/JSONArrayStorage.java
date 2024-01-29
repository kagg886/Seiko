package com.kagg886.seiko.util.storage;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.com.kagg886.seiko.util.storage
 * @className: JSONArrayStorage
 * @author: kagg886
 * @description: json数组存储类
 * @date: 2022/12/12 20:09
 * @version: 1.0
 */


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.kagg886.seiko.util.IOUtil;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class JSONArrayStorage extends JSONArray {
    private final String workdir;

    private static final ConcurrentHashMap<String,JSONArrayStorage> storagesCache = new ConcurrentHashMap<>(); //缓存池，减少从硬盘的读操作

    public static JSONArrayStorage obtain(String relativeDir) {
        if (storagesCache.containsKey(relativeDir)) {
            return storagesCache.get(relativeDir);
        }
        JSONArrayStorage s = null;
        try {
            s = new JSONArrayStorage(relativeDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        storagesCache.put(relativeDir,s);
        return s;
    }

    private JSONArrayStorage(String relativeDir) throws Exception {
        super(JSON.parseArray(getJSON(relativeDir)));
        this.workdir = relativeDir;
    }

    public boolean save() {
        try {
            IOUtil.writeStringToFile(workdir, this.toString());
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    private static String getJSON(String relativeDir) throws IOException {
        if (relativeDir.equals("")) {
            return "[]";
        }
        String string = IOUtil.loadStringFromFile(relativeDir);
        if (string.equals("")) {
            return "[]";
        }
        return string;
    }

}

