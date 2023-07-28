package com.kagg886.seiko.adapter;

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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

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

    private RandomAccessFile file;

    private final int maxLogNum = Integer.parseInt(SeikoApplication.globalConfig.getString("maxLogNum", "40"));

    public LogAdapter(File f) throws IOException {
        this.file = new RandomAccessFile(f, "r");
        this.log = new LimitedArrayList<>(maxLogNum);

        //第一步，预先读取最后一行
        long pointer = file.length() - 1;
        int line = 0;

        ArrayList<Byte> chars = new ArrayList<>();
        for (long i = pointer; i > 0 && line <= maxLogNum ; i--) {
            file.seek(i);
            byte chr = file.readByte();

            if (chr == '\n') { //一行，现在开始收集
                byte[] c = new byte[chars.size()];
                for (int j = 0; j < chars.size(); j++) {
                    c[j] = chars.get(j);
                }
                sendLogMessage(new String(c), true);
                line++;
                chars.clear();
                continue;
            }
            chars.add(0, chr);
        }

        new Thread(() -> {
            chars.clear();
            long seek = pointer;
            long len;

            try {
                file.seek(seek); //需要手动seek到最尾端
            } catch (IOException ignored) {}
            while (true) {
                try {
                    len = file.length();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                while (seek < len) {
                    seek++;
                    //实时监控，出现新内容立刻上报
                    byte byt = 0;
                    try {
                        byt = file.readByte();
                    } catch (IOException ignored) {
                    }

                    if (byt == '\n') {
                        byte[] c = new byte[chars.size()];
                        for (int j = 0; j < chars.size(); j++) {
                            c[j] = chars.get(j);
                        }
                        sendLogMessage(new String(c), false);
                        chars.clear();
                        continue;
                    }
                    chars.add(byt);
                }
            }
        }).start();
    }

    public void sendLogMessage(String message, boolean reverse) {
        Bundle b = new Bundle();
        try {
            b.putString("str", message);
            b.putBoolean("reverse", reverse);
        } catch (Exception e) {
            b.putString("str", "log error:this log contain binary");
        }
        Message msg = new Message();
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    //    private final Context ctx;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.getData().getBoolean("reverse", false)) {
                log.add(0, msg.getData().getString("str"));
            } else {
                log.add(msg.getData().getString("str"));
            }
            notifyDataSetChanged();
        }
    };


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
        TextView v = new TextView(SeikoApplication.getCurrentActivity());
        v.setText(log.get(i));
        return v;
    }
}
