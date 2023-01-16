package com.kagg886.seiko.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.event
 * @className: SnackBroadCast
 * @author: kagg886
 * @description: 代表一个广播事件，这个事件通知MainActivity发送SnackBar
 * @date: 2023/1/14 11:29
 * @version: 1.0
 */
public class SnackBroadCast extends BroadcastReceiver {
    public static final String TAG = "SnackBroadCast";

    private final View rootView;

    public SnackBroadCast(View avt) {
        this.rootView = avt;
    }

    public static void sendBroadCast(Context context, String message) {
        Intent broadcast = new Intent(TAG);
        broadcast.putExtra("msg", message);
        context.sendBroadcast(broadcast);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Snackbar.make(rootView, intent.getStringExtra("msg"), BaseTransientBottomBar.LENGTH_LONG).show();
    }
}
