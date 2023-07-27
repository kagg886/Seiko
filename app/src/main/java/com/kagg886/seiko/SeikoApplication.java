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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.bot.SeikoEncryptServiceFactory;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.ProtocolInjector;
import com.kagg886.seiko.util.storage.JSONObjectStorage;

import net.mamoe.mirai.internal.spi.EncryptService;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.Services;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Map;

import hamusuta.ProtocolUtils;

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
    public static Context context;

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
        SeikoApplication.context = this;
        ProtocolUtils.INSTANCE.injectProtocol();
        Services.INSTANCE.register(EncryptService.Factory.class.getName(), SeikoEncryptServiceFactory.class.getName(), () -> SeikoEncryptServiceFactory.Companion);
        globalConfig = PreferenceManager.getDefaultSharedPreferences(this);
//        fixProtocol();
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

    /*
     * @param :
     * @return void
     * @author kagg886
     * @description 修复协议，优先级为本地>云服务>默认
     * 云服务只能注入PHONE和PAD协议
     * @date 2023/06/05 12:26
     * @see https://github.com/cssxsh/fix-protocol-version/blob/main/src/main/kotlin/xyz/cssxsh/mirai/tool/FixProtocolVersion.kt
     */
    private void fixProtocol() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String protocol : new String[]{
//                        "ANDROID_PHONE",
                        "ANDROID_PAD"}) {
                    ProtocolInjector injector;
                    if (globalConfig.getBoolean("cloudProtocolInject", true)) {
                        BotConfiguration.MiraiProtocol protocol1 = BotConfiguration.MiraiProtocol.valueOf(protocol);
                        injector = new ProtocolInjector(protocol1);
                        try {
                            JSONObject newProtocol = JSON.parseObject(
                                    Jsoup.connect("https://ghproxy.com/https://raw.githubusercontent.com/RomiChan/protocol-versions/master/" + protocol.toLowerCase() + ".json").execute().body()
                            );
                            injector.setBuildVer(newProtocol.getString("sort_version_name"));
                            injector.setApkId(newProtocol.getString("apk_id"));
                            injector.setId(newProtocol.getLong("app_id"));
                            injector.setVer(newProtocol.getString("sort_version_name").substring(0, newProtocol.getString("sort_version_name").lastIndexOf(".")));
                            injector.setSdkVer(newProtocol.getString("sdk_version"));
                            injector.setMiscBitMap(newProtocol.getInteger("misc_bitmap"));
                            injector.setSubSigMap(newProtocol.getInteger("sub_sig_map"));
                            injector.setMainSigMap(newProtocol.getInteger("main_sig_map"));
                            injector.setSign(newProtocol.getString("apk_sign"));
                            injector.setBuildTime(newProtocol.getLong("build_time"));
                            injector.setSsoVersion(newProtocol.getInteger("sso_version"));
                            injector.setAppKey(newProtocol.getString("app_key"));
                            injector.setSupportsQRLogin(false);
                            injector.inject(protocol1);
                            Log.d(getClass().getName(), "协议:" + protocol + "云注入完成!" + newProtocol);
                        } catch (Exception e) {
                            Log.e(getClass().getName(), "协议" + protocol + "热修复失败!", e);
                        }
                    }

                    JSONObjectStorage storage = JSONObjectStorage.obtain(Paths.get(SeikoApplication.getSeikoApplicationContext().getExternalFilesDir("config").toString(), "customProtocol.json").toFile().getAbsolutePath());
                    //一定是String,String
                    for (Map.Entry<String, Object> objectEntry : storage.entrySet()) {
                        injector = JSON.parseObject(((String) objectEntry.getValue()), ProtocolInjector.class);
                        try {
                            injector.inject(BotConfiguration.MiraiProtocol.valueOf(objectEntry.getKey()));
                        } catch (Exception e) {
                            Log.i(getClass().getName(), "本地" + objectEntry.getKey() + "协议注入失败,已自动清除此信息");
                            storage.remove(objectEntry.getKey());
                            storage.save();
                        }
                    }
                }
            }
        }).start();
    }
}