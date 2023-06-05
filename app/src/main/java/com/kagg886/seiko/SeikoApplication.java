package com.kagg886.seiko;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.ProtocolInjector;
import com.kagg886.seiko.util.storage.JSONObjectStorage;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
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
        globalConfig = PreferenceManager.getDefaultSharedPreferences(this);
        fixProtocol();
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
                for (String protocol : new String[]{"ANDROID_PHONE", "ANDROID_PAD"}) {
                    ProtocolInjector injector;
                    if (globalConfig.getBoolean("cloudProtocolInject", true)) {
                        BotConfiguration.MiraiProtocol protocol1 = BotConfiguration.MiraiProtocol.valueOf(protocol);
                        injector = new ProtocolInjector(protocol1);
                        try {
                            JSONObject newProtocol = JSON.parseObject(
                                    Jsoup.connect("https://raw.githubusercontent.com/RomiChan/protocol-versions/master/" + protocol.toLowerCase() + ".json").execute().body()
                            );
                            injector.setApkId(newProtocol.getString("apk_id"));
                            injector.setId(newProtocol.getLong("app_id"));
                            injector.setVer(newProtocol.getString("sort_version_name"));
                            injector.setSdkVer(newProtocol.getString("sdk_version"));
                            injector.setMiscBitMap(newProtocol.getInteger("misc_bitmap"));
                            injector.setSubSigMap(newProtocol.getInteger("sub_sig_map"));
                            injector.setMainSigMap(newProtocol.getInteger("main_sig_map"));
                            injector.setSign(newProtocol.getString("apk_sign"));
                            injector.setBuildTime(newProtocol.getLong("build_time"));
                            injector.setSsoVersion(newProtocol.getInteger("sso_version"));
                            injector.inject(protocol1);
                            Log.d(getClass().getName(), "云协议:" + protocol + "云注入完成!" + newProtocol.toString());

                            //apkId = json.getValue("apk_id").jsonPrimitive.content
                            //id = json.getValue("app_id").jsonPrimitive.long
                            //ver = json.getValue("sort_version_name").jsonPrimitive.content
                            //sdkVer = json.getValue("sdk_version").jsonPrimitive.content
                            //miscBitMap = json.getValue("misc_bitmap").jsonPrimitive.int
                            //subSigMap = json.getValue("sub_sig_map").jsonPrimitive.int
                            //mainSigMap = json.getValue("main_sig_map").jsonPrimitive.int
                            //sign = json.getValue("apk_sign").jsonPrimitive.content.hexToBytes().toUHexString(" ")
                            //buildTime = json.getValue("build_time").jsonPrimitive.long
                            //ssoVersion = json.getValue("sso_version").jsonPrimitive.int

                            //{
                            //  "apk_id": "com.tencent.mobileqq",
                            //  "app_id": 537158635,
                            //  "sub_app_id": 537158635,
                            //  "app_key": "0S200MNJT807V3GE",
                            //  "sort_version_name": "8.9.53.10815",
                            //  "build_time": 1681901591,
                            //  "apk_sign": "a6b745bf24a2c277527716f6f36eb68d",
                            //  "sdk_version": "6.0.0.2538",
                            //  "sso_version": 19,
                            //  "misc_bitmap": 150470524,
                            //  "main_sig_map": 34869472,
                            //  "sub_sig_map": 66560,
                            //  "dump_time": 1683193286,
                            //  "protocol_type": 1
                            //}
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    JSONObjectStorage storage = JSONObjectStorage.obtain(Paths.get(SeikoApplication.getSeikoApplicationContext().getExternalFilesDir("config").toString(), "customProtocol.json").toFile().getAbsolutePath());
                    //一定是String,String
                    for (Map.Entry<String, Object> objectEntry : storage.entrySet()) {
                        injector = JSON.parseObject(((String) objectEntry.getValue()), ProtocolInjector.class);
                        injector.inject(BotConfiguration.MiraiProtocol.valueOf(objectEntry.getKey()));
                        Log.i(getClass().getName(), "Protocol:{" + objectEntry.getKey() + "}injected");
                    }
                }
            }
        }).start();
    }
}