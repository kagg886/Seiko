package com.kagg886.seiko.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kagg886.seiko.SeikoApplication;

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

    public static void sendBroadCast(@StringRes int message) {
        sendBroadCast(SeikoApplication.getSeikoApplicationContext().getText(message).toString());
    }
    public static void sendBroadCast(String message) {
        Intent broadcast = new Intent(TAG);
        broadcast.putExtra("msg", message);
        SeikoApplication.getSeikoApplicationContext().sendBroadcast(broadcast);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Snackbar msg = Snackbar.make(rootView, intent.getStringExtra("msg"), BaseTransientBottomBar.LENGTH_LONG);
        View view = msg.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(10);
        msg.show();
    }
}
