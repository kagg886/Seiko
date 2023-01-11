package com.kagg886.seiko.util.storage;

import androidx.annotation.NonNull;
import com.kagg886.seiko.util.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JSONObjectStorage extends JSONObject {

	private static final ConcurrentHashMap<String,JSONObjectStorage> storagesCache = new ConcurrentHashMap<>(); //缓存池，减少从硬盘的读操作

	public String indexKey(int a) {
		Iterator<String> b = keys();
		int k = 0;
		while (b.hasNext()) {
			String rtn = b.next();
			if (k == a) {
				return rtn;
			}
			k++;
		}
		return null;
	}

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
		super(getJSON(relativeDir));
		this.workdir = relativeDir;
	}

	public String getWorkdir() {
		return workdir;
	}


	public JSONObject put(@NonNull @NotNull String name, String value) {
		try {
			return super.put(name, value);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized boolean save() {
		try {
			IOUtil.writeStringToFile(workdir, this.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
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

}
