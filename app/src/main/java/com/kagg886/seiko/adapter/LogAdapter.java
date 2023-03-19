package com.kagg886.seiko.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.util.LimitedArrayList;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
    private static final char SPLIT_STR = '\u001B';
    private final LimitedArrayList<String> log;
    private final Context ctx;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            log.add(msg.getData().getString("str"));
            notifyDataSetChanged();
        }
    };
    private FileReader reader;

    public LogAdapter(Context ctx, File logFile) {
        log = new LimitedArrayList<>(Integer.parseInt(SeikoApplication.globalConfig.getString("maxLogNum", "40")));
        this.ctx = ctx;
        try {
            reader = new FileReader(logFile);
            new Thread(() -> {
                char[] buf = new char[1];
                while (true) {
                    StringBuilder builder = new StringBuilder();
                    boolean isCollected = false;
                    try {
                        while (reader.read(buf) != -1) {
                            if (buf[0] == SPLIT_STR) {
                                if (isCollected) { //遇到配对的符号了，切割然后合成大字符串

                                    Bundle b = new Bundle();
                                    b.putString("str",builder.substring(3));
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.setData(b);
                                    mHandler.sendMessage(msg);
                                    builder = new StringBuilder();
                                    isCollected = false;
                                } else { //未配对，开启配对
                                    isCollected = true;
                                    continue;
                                }
                            }
                            if (isCollected) {
                                builder.append(buf);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (Exception e) {

        }
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
