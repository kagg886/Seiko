package com.kagg886.seiko.plugin.api;

import com.kagg886.seiko.plugin.SeikoDescription;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.plugin.api
 * @className: SeikoPluginLoader
 * @author: kagg886
 * @description: APP实现此类，当Bot上线，下线，加载时调用此方法
 * @date: 2022/12/22 14:56
 * @version: 1.0
 */
public abstract class SeikoPlugin {

    private File f;

    public File getFile() {
        return f;
    }

    public void setFile(File f) {
        this.f = f;
    }

    public abstract void onBotGoLine(long botQQ);

    @Deprecated
    public abstract void onBotOffLine(long botQQ);

    public abstract void onLoad(Object context);

    public abstract SeikoDescription getDescription();
}
