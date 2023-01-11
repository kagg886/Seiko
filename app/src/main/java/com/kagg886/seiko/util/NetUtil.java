package com.kagg886.seiko.util;

import androidx.annotation.NonNull;

import net.mamoe.mirai.internal.deps.okhttp3.Call;
import net.mamoe.mirai.internal.deps.okhttp3.Callback;
import net.mamoe.mirai.internal.deps.okhttp3.OkHttpClient;
import net.mamoe.mirai.internal.deps.okhttp3.Request;
import net.mamoe.mirai.internal.deps.okhttp3.Response;

import java.io.IOException;

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
