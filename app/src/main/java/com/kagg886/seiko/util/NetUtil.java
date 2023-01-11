package com.kagg886.seiko.util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NetUtil {
    public static void downloadFromUrlAsync(String url, Callback callback)  {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request req = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(req);
        call.enqueue(callback);
    }
}
