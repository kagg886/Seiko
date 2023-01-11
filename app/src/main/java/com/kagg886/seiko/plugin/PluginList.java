package com.kagg886.seiko.plugin;

import android.content.Context;
import com.kagg886.seiko.dic.DICPlugin;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.util.IOUtil;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ServiceLoader;


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
                f.delete();
                DialogBroadCast.sendBroadCast(ctx, f.getName() + "加载失败,此插件已自动删除", IOUtil.getException(e));
            }
        }
        ArrayList<SeikoPlugin> remove = new ArrayList<>(); //可恶的ConcurrentModificationException
        for (SeikoPlugin p : this) {
            try {
                p.onLoad(ctx);
            } catch (Exception e) {
                remove.add(p);
                p.getFile().delete();
                DialogBroadCast.sendBroadCast(ctx, p.getDescription().getId() + "初始化失败", IOUtil.getException(e));
            }
        }
        for (SeikoPlugin p : remove) {
            remove(p);
        }
    }

    public void loadClass(File f) {
        if (f.getName().endsWith(".apk") || f.getName().endsWith(".zip") || f.getName().endsWith(".jar")) {
            DexClassLoader classLoader = new DexClassLoader(f.getAbsolutePath(), ctx.getCacheDir().getAbsolutePath(), null, getClass().getClassLoader());
            ServiceLoader<SeikoPlugin> load = ServiceLoader.load(SeikoPlugin.class, classLoader);

            SeikoPlugin plugin = load.iterator().next();
            if (plugin == null) {
                throw new IllegalArgumentException(f.getName() + "不是一个合法的Seiko插件");
            }
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


