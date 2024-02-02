package com.kagg886.seiko;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.kagg886.seiko.bot.SeikoEncryptServiceFactory;
import com.kagg886.seiko.util.AntiDetect;
import com.kagg886.seiko.util.IOUtil;
import hamusuta.ProtocolUtils;
import net.mamoe.mirai.internal.spi.EncryptService;
import net.mamoe.mirai.utils.Services;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Map;

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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AntiDetect.init();
    }

    /*
     * @param :
     * @return Activity
     * @author kagg886
     * @description 获取正在运行的Activity。请不要在不确定Activity类型的地方调用
     * @date 2023/02/24 23:26
     */
    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    public static Activity getCurrentActivity() {
        Activity current = null;
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(
                    null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class<?> activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    current = (Activity) activityField.get(activityRecord);
                }
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d("SeikoApplication", "access getCurrentActivity:" + current);
        return current;
    }


    /*
     * @param :
     * @return Application
     * @author kagg886
     * @description 调用隐藏方法获取ApplicationContext
     * @date 2023/02/23 14:35
     */
    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    public static SeikoApplication getSeikoApplicationContext() {
        Application application = null;
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Method currentApplicationMethod = atClass.getDeclaredMethod("currentApplication");
            currentApplicationMethod.setAccessible(true);
            application = (Application) currentApplicationMethod.invoke(null);
        } catch (Exception ignored) {
        }
        if (application != null) return (SeikoApplication) application;
        try {
            Class<?> atClass = Class.forName("android.app.AppGlobals");
            Method currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication");
            currentApplicationMethod.setAccessible(true);
            application = (Application) currentApplicationMethod.invoke(null);
        } catch (Exception ignored) {
        }
        return (SeikoApplication) application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ProtocolUtils.INSTANCE.injectProtocol();
        Services.INSTANCE.register(EncryptService.Factory.class.getName(), SeikoEncryptServiceFactory.class.getName(), () -> SeikoEncryptServiceFactory.Companion);
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