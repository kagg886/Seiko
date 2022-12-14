package com.kagg886.seiko.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.bot.LoginThread;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BotRunnerService extends Service {

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static BotRunnerService INSTANCE;

    private MainActivity avt;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("DEBUG", "onBind被调用");
        return new Bridge();
    }


    @Override
    public void onDestroy() {
        avt = null;
    }

    public void login(JSONObject target, TextView nick, SwitchCompat sw) {
        executor.execute(new LoginThread(avt, target, sw, nick));
    }

    public void setActivity(MainActivity avt) {
        this.avt = avt;
    }

    public class Bridge extends Binder {
        public BotRunnerService getService() {
            return BotRunnerService.this;
        }
    }
}