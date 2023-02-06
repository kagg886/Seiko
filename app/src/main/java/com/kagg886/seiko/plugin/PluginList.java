package com.kagg886.seiko.plugin;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.kagg886.seiko.dic.DICPlugin;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import dalvik.system.DexClassLoader;
import net.mamoe.mirai.Bot;

import java.io.File;
import java.util.*;


/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.plugin
 * @className: PluginList
 * @author: kagg886
 * @description: 存放Plugin用的List
 * @date: 2022/12/22 15:27
 * @version: 1.0
 */
public class PluginList extends ArrayList<SeikoPlugin> {

    private final Context ctx; //Service的Context

    public PluginList(Context ctx) {
        this.ctx = ctx;
        refresh();
    }

    public void refresh() {
        clear();
        add(new DICPlugin());
        for (File f : Objects.requireNonNull(ctx.getExternalFilesDir("plugin").listFiles())) {
            try {
                loadClass(f);
            } catch (Throwable e) {
                if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("badPluginAutoDel", false)) {
                    f.delete();
                }
                DialogBroadCast.sendBroadCast(ctx, f.getName() + "加载失败,此插件已自动删除", IOUtil.getException(e));
            }
        }
        ArrayList<SeikoPlugin> remove = new ArrayList<>(); //可恶的ConcurrentModificationException
        for (SeikoPlugin plugin : this) {
            try {
                plugin.onLoad(ctx);
                //bot登录后若添加新插件则直接加载
                Bot.getInstances().stream().filter(Bot::isOnline).forEach(bot -> {
                    HashMap<String, Long> a = BotRunnerService.INSTANCE.getLastLoad();
                    if (!Objects.equals(a.getOrDefault(plugin.getDescription().getId(), 0L), bot.getId())) {
                        try {
                            plugin.onBotGoLine(bot.getId());
                            a.put(plugin.getDescription().getId(), bot.getId());
                        } catch (Throwable e) {
                            bot.getLogger().error("加载插件:" + plugin.getDescription().getName() + "(" + plugin.getDescription().getId() + ")发生错误!", e);
                            SnackBroadCast.sendBroadCast(ctx, "初始化:" + plugin.getDescription().getName() + "时发生错误,请前往bot日志查看。");
                        }
                    }
                });
            } catch (Exception e) {
                remove.add(plugin);
                plugin.getFile().delete();
                DialogBroadCast.sendBroadCast(ctx, plugin.getDescription().getId() + "初始化失败", IOUtil.getException(e));
            }
        }
        for (SeikoPlugin p : remove) {
            remove(p);
        }
    }

    public void loadClass(File f) {
        if (f.getName().endsWith(".apk") || f.getName().endsWith(".zip") || f.getName().endsWith(".jar")) {
            DexClassLoader classLoader = new DexClassLoader(f.getAbsolutePath(), ctx.getCacheDir().getAbsolutePath(), null, getClass().getClassLoader());
            Iterator<SeikoPlugin> load = ServiceLoader.load(SeikoPlugin.class, classLoader).iterator();

            if (!load.hasNext()) {
                throw new IllegalArgumentException(f.getName() + "不是一个合法的Seiko插件");
            }
            SeikoPlugin plugin = load.next();
            plugin.setFile(f);
            add(plugin);
        }
    }

    @Override
    public boolean add(SeikoPlugin seikoPlugin) {
        for (SeikoPlugin has : this) {
            if (has.getDescription().getId().equals(seikoPlugin.getDescription().getId())) {
                throw new ArrayStoreException(String.format("插件发生冲突:[%s(%s)]-[%s(%s)]",
                        has.getDescription().getId(),
                        has.getFile().getAbsolutePath(),
                        seikoPlugin.getDescription().getId(),
                        seikoPlugin.getFile().getAbsolutePath()));
            }
        }
        return super.add(seikoPlugin);
    }
}


