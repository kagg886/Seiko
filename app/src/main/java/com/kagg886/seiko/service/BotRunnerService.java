package com.kagg886.seiko.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.bot.LoginThread;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.plugin.PluginList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.service
 * @className: PluginList
 * @author: kagg886
 * @description: BOT托管服务，提供Activity与Mirai-Core交互的接口
 * @date: 2022/12/19 16:48
 * @version: 1.0
 */
public class BotRunnerService extends Service {

    //通知id
    private static final String CHANNEL_ID = "114514";
    //Service静态上下文。由于技术不够，只能这么写了
    public static BotRunnerService INSTANCE;
    //线程池，用于运行Bot线程
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    //通知图标
    private Bitmap icon;

    //内部维护的插件列表
    private PluginList seikoPluginList;
    private final HashMap<String, Long> lastLoad = new HashMap<>();

    private NotificationManager notificationManager;

    private PendingIntent pIntent;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            notificationManager.notify(114514, getNotification("已运行" + msg.what + "秒"));
        }
    };

    public PluginList getSeikoPluginList() {
        return seikoPluginList;
    }

    public HashMap<String, Long> getLastLoad() {
        return lastLoad;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        INSTANCE = this;
        startForeground(114514, getNotification("Seiko启动成功"));
        new Thread(() -> {
            int i = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mHandler.sendEmptyMessage(i);
                i++;
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
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        seikoPluginList = new PluginList(this);
        NotificationChannel chan = new NotificationChannel(CHANNEL_ID, "botRunner", NotificationManager.IMPORTANCE_MIN);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(chan);
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        Intent i = new Intent(this, MainActivity.class);
        int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_IMMUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        pIntent = PendingIntent.getActivity(this, UUID.randomUUID().hashCode(), i, flag);
        SnackBroadCast.sendBroadCast("绑定服务成功");
    }

    public void login(JSONObject target, TextView nick, SwitchCompat sw) {
        executor.execute(new LoginThread(target, sw, nick));
    }

    public LoginThread getThreadByBot(long qq) {
        return executor.getQueue().stream()
                .map((v) -> (LoginThread) v).filter(v -> v.getBot() == qq)
                .findFirst()
                .orElse(null);
    }

    private Notification getNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("Seiko")
                .setContentText(content)
                .setWhen(System.currentTimeMillis())//通知显示时间
                .setOnlyAlertOnce(true) //不加这行等着吵死你 哼唧
                .setSmallIcon(R.drawable.ic_launcher).setOngoing(true).setLargeIcon(icon);
        builder.setContentIntent(pIntent);
        return builder.build();
    }

    private PowerManager.WakeLock mWakeLock;
    /**
     * 同步方法   得到休眠锁
     *
     * @param context
     * @return
     */
    synchronized private void getLock(Context context) {
        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BotRunnerService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis((System.currentTimeMillis()));
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour == 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWakeLock!=null){
            if(mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            mWakeLock=null;
        }
    }
}