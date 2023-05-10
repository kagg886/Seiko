package com.kagg886.seiko.dic.mirai_console;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.DictionaryReg;
import com.kagg886.seiko.dic.bridge.DictionaryListener;
import com.kagg886.seiko.util.IgnoreSSL;
import com.kagg___.seiko.dic.mirai_console.ConsolePlugin.BuildConfig;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.message.data.Message;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.mirai_console
 * @className: PluginLoader
 * @author: kagg886
 * @description: 插件主类
 * @date: 2023/1/27 17:38
 * @version: 1.0
 */
public class PluginLoader extends JavaPlugin implements DictionaryListener {

    public static final PluginLoader INSTANCE = new PluginLoader();

    private PluginLoader() {
        super(new JvmPluginDescriptionBuilder("com.kagg886.seiko.dic.mirai_console", BuildConfig.DIC_VERSION)
                .name("Seiko Dictionary Plugin")
                .info("可以在PC上运行伪代码的插件\n项目地址:https://github.com/kagg886/Seiko")
                .author("kagg886 and All Seiko Contributors")
                .build());
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage api) {
        //首先忽略掉ssl证书错误
        IgnoreSSL.init();
        super.onLoad(api);

        //开始初始化dic环境
        File tmp = getConfigFolderPath().resolve("dic").toFile();
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        DictionaryEnvironment.getInstance().setDicRoot(tmp);
        tmp = getConfigFolderPath().resolve("dicData").toFile();
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        DictionaryEnvironment.getInstance().setErrorListener(this);
        DictionaryEnvironment.getInstance().setDicConfigPoint(getConfigFolderPath().resolve("dicList.json").toFile().getAbsolutePath());
        DictionaryEnvironment.getInstance().setDicData(tmp.toPath());

        //检查更新
        new Thread(() -> {
            try {
                JSONObject object = JSON.parseObject(
                        Jsoup.connect("https://api.github.com/repos/kagg886/Seiko/releases/latest")
                                .ignoreContentType(true)
                                .timeout(10000)
                                .execute().body());
                String newVer = object.getJSONArray("assets")
                        .stream()
                        .map(ary -> ((JSONObject) ary).getString("browser_download_url"))
                        .filter((str) -> str.contains("ConsolePlugin"))
                        .findFirst().get().split("-")[1];
                if (!newVer.startsWith(BuildConfig.DIC_VERSION)) {
                    getLogger().info(String.format("发现更新:%s->%s",BuildConfig.DIC_VERSION,newVer));
                    getLogger().info(object.getString("body"));
                    getLogger().info("下载地址:" + object.getString("html_url"));
                } else {
                    getLogger().info("当前为最新版本!");
                }
            } catch (Exception e) {
                getLogger().error("更新检测失败!");
            }
        }).start();

        getLogger().info("Environment初始化成功!");
    }

    @Override
    public void onEnable() {
        reloadPluginConfig(SeikoPluginConfig.INSTANCE);
        //注册指令集
        CommandManager.INSTANCE.registerCommand(CommandInstance.INSTANCE, false);

        EventChannel<?> channel = GlobalEventChannel.INSTANCE.parentScope(INSTANCE);
        boolean refresh = SeikoPluginConfig.INSTANCE.getAlwaysRefreshOnceMessageGetting();

        DictionaryReg.reg(channel,
                refresh,
                (logger,res) -> {
                    logger.warning("插件解析中存在问题！请检查无法被启用的插件");
                    res.err.forEach(logger::warning);
                }
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onWarn(File p, String message) {
        PluginLoader.INSTANCE.getLogger().warning(p.getName() + "发现一个潜在性问题:s" + message);
    }
}
