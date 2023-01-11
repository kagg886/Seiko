package com.kagg886.seiko.plugin;

import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.service.BotRunnerService;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

import javax.xml.transform.ErrorListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    private Context ctx; //Service的Context

    public PluginList(Context ctx) {
        this.ctx = ctx;
        refresh();
    }

    public void refresh() {
        clear();
        for (File f : ctx.getExternalFilesDir("plugin").listFiles()) {
            try {
                loadClass(f);
            } catch (Throwable e) {
                AlertDialog dialog1 = new AlertDialog.Builder(BotRunnerService.avt).setTitle(f.getName() + "加载失败")
                        .setMessage(e.getMessage()).create();
                dialog1.show();
                Log.w("DEBUG",e);
            }
        }
    }

    public void loadClass(File f) throws Throwable {
        if (f.getName().endsWith(".apk") || f.getName().endsWith(".zip")) {
            DexClassLoader classLoader = new DexClassLoader(f.getAbsolutePath(), ctx.getCacheDir().getAbsolutePath(), null, getClass().getClassLoader());
            ServiceLoader<SeikoPlugin> load = ServiceLoader.load(SeikoPlugin.class, classLoader);
            for (SeikoPlugin o : load) {
                this.add(o);
            }
        }
    }
}


