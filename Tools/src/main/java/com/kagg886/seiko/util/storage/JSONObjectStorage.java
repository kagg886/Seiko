package com.kagg886.seiko.util.storage;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.seiko.util.IOUtil;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JSONObjectStorage extends JSONObject {

	private static final ConcurrentHashMap<String,JSONObjectStorage> storagesCache = new ConcurrentHashMap<>(); //缓存池，减少从硬盘的读操作

	public static void destroy(String workdir) {
		for (Map.Entry<String,JSONObjectStorage> entry : storagesCache.entrySet()) {
			if (entry.getKey().equals(workdir)) {
				storagesCache.remove(workdir);
			}
		}
	}

	public static JSONObjectStorage obtain(String relativeDir) {
		if (storagesCache.containsKey(relativeDir)) {
			return storagesCache.get(relativeDir);
		}
		JSONObjectStorage s = null;
		try {
			s = new JSONObjectStorage(relativeDir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		storagesCache.put(relativeDir,s);
		return s;
	}

	private final String workdir;

	private JSONObjectStorage(String relativeDir) throws Exception {
		super(JSON.parseObject(getJSON(relativeDir)));
		this.workdir = relativeDir;
	}

	public String getWorkdir() {
		return workdir;
	}


	private static String getJSON(String relativeDir) throws IOException {
        if (relativeDir.equals("")) {
            return "{}";
        }
        String string = IOUtil.loadStringFromFile(relativeDir);
        if (string.equals("")) {
            return "{}";
        }
        return string;
    }

	public synchronized boolean save() {
		try {
			IOUtil.writeStringToFile(workdir, this.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
    }

    public Object put(String name, String value) {
        try {
            return super.put(name, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

//    public JSONObject optJSONObject(String name, JSONObject jsonObject) {
//        if (isNull(name)) {
//            return jsonObject;
//        }
//        return super.optJSONObject(name);
//    }
}
