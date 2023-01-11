package com.kagg886.seiko.plugin;

import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.kagg886.seiko.dic.DICPlugin;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
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
                AlertDialog dialog1 = new AlertDialog.Builder(ctx).setTitle(f.getName() + "加载失败")
                        .setMessage(e.getMessage()).create();
                dialog1.show();
                Log.w("DEBUG", e);
            }
        }
        for (SeikoPlugin p : this) {
            try {
                p.onLoad(ctx);
            } catch (Exception e) {
                remove(p);
                AlertDialog dialog1 = new AlertDialog.Builder(ctx).setTitle(p.getDescription().getId() + "加载失败")
                        .setMessage(e.getMessage()).create();
                dialog1.show();
                Log.w("Seiko", e);
            }
        }
    }

    public void loadClass(File f) {
        if (f.getName().endsWith(".apk") || f.getName().endsWith(".zip")) {
            DexClassLoader classLoader = new DexClassLoader(f.getAbsolutePath(), ctx.getCacheDir().getAbsolutePath(), null, getClass().getClassLoader());
            ServiceLoader<SeikoPlugin> load = ServiceLoader.load(SeikoPlugin.class, classLoader);
            for (SeikoPlugin o : load) {
                this.add(o);
            }
        }
    }
}


