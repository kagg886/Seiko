package com.kagg886.seiko.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.LimitedArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.adapter
 * @className: LogAdapter
 * @author: kagg886
 * @description: 显示日志用的
 * @date: 2023/1/10 11:34
 * @version: 1.0
 */
public class LogAdapter extends BaseAdapter {
    private LimitedArrayList<String> log;
    private Context ctx;

    private BufferedReader reader;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            notifyDataSetChanged();
        }
    };

    public LogAdapter(Context ctx, File logFile) {
        log = new LimitedArrayList<>(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ctx).getString("maxLogNum","40")));
        this.ctx = ctx;

        try {
            reader = new BufferedReader(new FileReader(logFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            String a = null;
            boolean needDelay = false;
            while (true) {
                try {
                    if (needDelay) {
                        Thread.sleep(1000);
                    }
                    a = reader.readLine();
                } catch (Exception e) {}
                if (a == null) {
                    needDelay = true;
                    continue;
                } else {
                    needDelay = false;
                }
                log.add(a);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public int getCount() {
        return log.size();
    }

    @Override
    public Object getItem(int i) {
        return log.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView v = new TextView(ctx);
        v.setText(log.get(i));
        return v;
    }
}
