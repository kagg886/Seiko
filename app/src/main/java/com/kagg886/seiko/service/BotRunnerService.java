package com.kagg886.seiko.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.bot.LoginThread;
import com.kagg886.seiko.plugin.PluginList;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BotRunnerService extends Service {

    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static BotRunnerService INSTANCE;

    private static final String CHANNEL_ID = "114514";

    private Bitmap icon;

    private PluginList seikoPluginList;

    @SuppressLint("StaticFieldLeak")
    public static MainActivity avt;


    public PluginList getSeikoPluginList() {
        return seikoPluginList;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        INSTANCE = this;
        startForeground(114514, getNotification("Seiko启动成功"));
        new Thread(() -> {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final int[] i = {0};
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                avt.runOnUiThread(() -> {
                    notificationManager.notify(114514,getNotification("已运行" + i[0] + "秒"));
                    i[0]++;
                });
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        seikoPluginList = new PluginList(this);
        NotificationChannel chan = new NotificationChannel(CHANNEL_ID, "botRunner", NotificationManager.IMPORTANCE_DEFAULT);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);

        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    public void login(JSONObject target, TextView nick, SwitchCompat sw) {
        executor.execute(new LoginThread(avt, target, sw, nick));
    }

    private Notification getNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(avt, CHANNEL_ID);
        builder.setContentTitle("Seiko")
                .setContentText(content)
                .setWhen(System.currentTimeMillis())//通知显示时间
                .setSmallIcon(R.mipmap.ic_launcher).setOngoing(true).setPriority(NotificationCompat.PRIORITY_MAX).setLargeIcon(icon);
        return builder.build();
    }
}