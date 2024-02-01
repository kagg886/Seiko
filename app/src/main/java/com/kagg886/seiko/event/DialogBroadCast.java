package com.kagg886.seiko.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.activity.MainActivity;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.event
 * @className: BasicBroadCast
 * @author: kagg886
 * @description: 接收错误事件的BroadCast。使用动态注册来保证Context为Activity的Context
 * @date: 2023/1/11 14:23
 * @version: 1.0
 */
public class DialogBroadCast extends BroadcastReceiver {

    public static final String TAG = "DialogBroadCast";

    private final MainActivity avt;

    public DialogBroadCast(MainActivity avt) {
        //Shit Android
        this.avt = avt;
    }

    public static void sendBroadCast(String name, String message) {
        Intent broadcast = new Intent(TAG);
        broadcast.putExtra("error", message);
        broadcast.putExtra("name", name);
        SeikoApplication.getSeikoApplicationContext().sendBroadcast(broadcast);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AlertDialog dialog1 = new AlertDialog.Builder(SeikoApplication.getCurrentActivity()).setTitle(intent.getStringExtra("name"))
                .setMessage(intent.getStringExtra("error")).create();
        dialog1.show();
    }
}
