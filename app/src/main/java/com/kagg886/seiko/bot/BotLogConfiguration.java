package com.kagg886.seiko.bot;

import android.content.Context;
import android.os.Build;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.util.DeviceInfoBuilder;
import com.kagg886.seiko.util.IOUtil;
import kotlin.jvm.functions.Function1;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.modules.SerializersModule;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.DeviceInfo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.bot
 * @className: SupportAndroidBotConfiguration
 * @author: kagg886
 * @description: 将LogFile暴露出来的BotConfiguration
 * @date: 2023/1/11 9:32
 * @version: 1.0
 */
public class BotLogConfiguration extends BotConfiguration {

    private final File logFile;

    //下面4个为提前准备好的反射参数
    private static final Method deserialize;
    private static final Method serialize;
    private static final Object instance;

    private static final Json json;

    static {
        try {
            Field DeviceInfoManager = Class.forName("net.mamoe.mirai.utils.DeviceInfoManager").getDeclaredField("INSTANCE");
            instance = DeviceInfoManager.get(null);

            json = (Json) instance.getClass().getMethod("getFormat$mirai_core_api").invoke(instance);


            serialize = instance.getClass().getMethod("serialize", DeviceInfo.class, Json.class);
            deserialize = instance.getClass().getMethod("deserialize", String.class, Json.class);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public BotLogConfiguration(Long bot) {
        super();
        Context avt = SeikoApplication.getSeikoApplicationContext();
        Path parentPath = avt.getExternalFilesDir("bots").toPath().resolve(String.valueOf(bot));
        setWorkingDir(parentPath.toFile());
        File p = parentPath.resolve("device.json").toFile();

        DeviceInfo info;
        try {
            if (!p.exists()) {
                //开始创建设备信息并保存
                p.getParentFile().mkdirs();
                p.createNewFile();

                DeviceInfoBuilder builder = new DeviceInfoBuilder();
                builder.display(Build.DISPLAY).product(Build.PRODUCT).device(Build.DEVICE).board(Build.BOARD).brand(Build.BRAND).model(Build.MODEL).bootloader(Build.BOOTLOADER).fingerprint(Build.FINGERPRINT);
                builder.procVersion("Linux version 4.14.186-perf-ge723f9b56398 (builder@non-pangu-pod-cfpkm) (Android (6443078 based on r383902) clang version 11.0.1 (https://android.googlesource.com/toolchain/llvm-project b397f81060ce6d701042b782172ed13bee898b79), LLD 11.0.1 (/buildbot/tmp/tmp6_m7QH b397f81060ce6d701042b782172ed13bee898b79)) #1 SMP PREEMPT Mon Nov 21 11:16:54 CST 2022");
                info = builder.build();

                //手动保存设备信息
                //DeviceInfoManager.INSTANCE.serialize(info,null);
                String deviceStr = (String) serialize.invoke(instance, info, json);
                IOUtil.writeStringToFile(p.getAbsolutePath(), deviceStr);
            } else {
                String deviceStr = IOUtil.loadStringFromFile(p.getAbsolutePath());
                info = (DeviceInfo) deserialize.invoke(instance, deviceStr, json);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        setDeviceInfo(bot1 -> info);


        setLoginSolver(new AndroidSolver((MainActivity) SeikoApplication.getCurrentActivity()));

        File f1;
        if (SeikoApplication.globalConfig.getBoolean("mergeAllLogs", false)) {
            f1 = avt.getExternalFilesDir("logs").toPath().resolve(new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + ".log").toFile();
        } else {
            f1 = parentPath.resolve("log").resolve(new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + ".log").toFile();
        }
        if (!f1.exists()) {
            f1.getParentFile().mkdirs();
            try {
                f1.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        redirectBotLogToFile(f1);
        redirectNetworkLogToFile(f1);
        enableContactCache();
        this.logFile = f1;
    }

    public File getLogFile() {
        return logFile;
    }
}
