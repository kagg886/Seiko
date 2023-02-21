package com.kagg886.seiko;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import com.kagg886.seiko.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko
 * @className: CrashHandler
 * @author: kagg886
 * @description: 全局崩溃检测
 * @date: 2023/1/12 14:50
 * @version: 1.0
 */
public class SeikoApplication extends Application implements Runnable, Thread.UncaughtExceptionHandler {

    public static SharedPreferences globalConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        globalConfig = PreferenceManager.getDefaultSharedPreferences(this);
        Thread.setDefaultUncaughtExceptionHandler(this);
        new Handler(Looper.getMainLooper()).post(this);
    }

    @Override
    public void run() {
        //主线程崩溃检测
        while (true) {
            try {
                Looper.loop();
            } catch (Throwable th) {
                writeCrash(th);
            }
        }
    }

    private void writeCrash(Throwable th) {
        Log.w("ERROR", th);
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        File wr = getExternalFilesDir("crash").toPath().resolve(System.currentTimeMillis() + ".log").toFile();
        try {
            FileWriter fileWriter = new FileWriter(wr);
            fileWriter.append("---SeikoCrashLogInfo---");
            fileWriter.append("\nCrashTime:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
            fileWriter.append("\n---DeviceInfo---");
            fileWriter.append("\nAndroid-Version:").append(Build.VERSION.RELEASE);
            fileWriter.append("\nModel:").append(Build.MODEL);
            fileWriter.append("\nAPP-Version:").append(info.versionName).append("(").append(String.valueOf(info.getLongVersionCode())).append(")");
            fileWriter.append("\nMirai-Version:").append(BuildConfig.MIRAI_VERSION);
            fileWriter.append("\n---StackTrace---\n");
            fileWriter.append(IOUtil.getException(th));
            fileWriter.flush();
            fileWriter.close();
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        //子线程崩溃检测
        writeCrash(e);
    }
}