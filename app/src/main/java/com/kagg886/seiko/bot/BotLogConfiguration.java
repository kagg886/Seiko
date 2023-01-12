package com.kagg886.seiko.bot;

import com.kagg886.seiko.activity.MainActivity;
import net.mamoe.mirai.utils.BotConfiguration;

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

    private final File logFile;

    public BotLogConfiguration(Long bot, MainActivity avt) {
        super();
        Path parentPath = avt.getExternalFilesDir("bots").toPath().resolve(String.valueOf(bot));
        setWorkingDir(parentPath.toFile());
        File p = parentPath.resolve("device.json").toFile();
        if (!p.exists()) {
            p.getParentFile().mkdirs();
            try {
                p.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        fileBasedDeviceInfo(p.getAbsolutePath());
        setLoginSolver(new AndroidSolver(avt));


        File f1 = parentPath.resolve("log").resolve(new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + ".log").toFile();
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
        this.logFile = f1;
    }

    public File getLogFile() {
        return logFile;
    }
}
