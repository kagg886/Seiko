package com.kagg886.seiko.bot;

import android.content.Context;
import android.os.Build;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.util.IOUtil;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.DeviceInfo;
import net.mamoe.mirai.utils.DeviceInfoBuilder;

import java.io.File;
import java.io.IOException;
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

    private File logFile;


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

                IOUtil.writeStringToFile(p.getAbsolutePath(), DeviceInfo.serializeToString(info));
            } else {
                info = DeviceInfo.from(p);
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
