package com.kagg886.seiko.plugin.api;

import androidx.annotation.Keep;
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
@Keep
public abstract class SeikoPlugin {

    private File f;

    public File getFile() {
        return f;
    }

    public void setFile(File f) {
        this.f = f;
    }

    /**
     * @param botQQ: 机器人QQ
     * @return void
     * @author kagg886
     * @description 当bot上线时调用此方法，当新插件被添加时，也会调用此方法。请自行调用Mirai-Core的API实现解析
     * @date 2023/03/06 17:28
     */
    public abstract void onBotGoLine(long botQQ);

    /*
     * @param context: Service的上下文Context
     * @return void
     * @author kagg886
     * @description 当插件被加载时调用。注意：此时bot可能尚未处于登录状态
     * @date 2023/03/06 17:29
     */
    public abstract void onLoad(Object context);

    /*
     * @param :
     * @return void
     * @author kagg886
     * @description 插件被卸载时(用户手动删除)时调用
     * TODO 将来此方法可能会变成abstract
     * @date 2023/03/06 17:31
     */
    public void onUnLoad() {

    }

    /*
     * @param :
     * @return SeikoDescription
     * @author kagg886
     * @description 获取插件的描述
     * @date 2023/03/06 17:31
     */
    public abstract SeikoDescription getDescription();
}
