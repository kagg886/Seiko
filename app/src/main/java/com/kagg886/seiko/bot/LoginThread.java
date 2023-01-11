package com.kagg886.seiko.bot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.json.JSONObject;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.bot
 * @className: LoginThread
 * @author: kagg886
 * @description: 登录用线程
 * @date: 2022/12/13 12:25
 * @version: 1.0
 */
public class LoginThread extends Thread {
    private final Bot bot;
    private final MainActivity avt;

    private final SwitchCompat sw;
    private final TextView nick;

    private final AlertDialog dialog;

    private final Handler dialogController = new Handler(Looper.getMainLooper()) { //Dialog控制的Handler
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 2:
                    dialog.show();
                    break;
                case 0:
                    avt.snack("Bot:" + bot.getId() + "登录成功");
                    nick.setText(bot.getNick());
                    dialog.dismiss();
                    break;
                case 1:
                    avt.snack("发生异常:" + ((Throwable) msg.getData().getSerializable("exception")).getMessage());
                    sw.setChecked(false);
                    dialog.dismiss();
                    break;
            }
        }
    };

    @SuppressLint("DefaultLocale")
    public LoginThread(MainActivity avt, JSONObject botConfig, SwitchCompat sw, TextView nick) {
        this.avt = avt;
        this.nick = nick;
        this.sw = sw;

        Long uin = botConfig.optLong("uin");
        String pass = botConfig.optString("pass");
        BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.valueOf(botConfig.optString("platform", "ANDROID_PHONE"));

        dialog = new AlertDialog.Builder(avt)
                .setTitle("登录中...(" + uin + ")")
                .setCancelable(false)
                .setMessage("请稍等片刻...")
                .create();
        BotLogConfiguration configuration = new BotLogConfiguration(uin, avt);
        configuration.setProtocol(protocol);
        bot = BotFactory.INSTANCE.newBot(uin, pass, configuration);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void run() {
        try {
            dialogController.sendEmptyMessage(2);
            bot.login();
            dialogController.sendEmptyMessage(0);
            JSONArrayStorage s = JSONArrayStorage.obtain(avt.getExternalFilesDir("config").getAbsolutePath() + "/botList.json");
            for (int i = 0; i < s.length(); i++) {
                JSONObject b = s.optJSONObject(i);
                if (b.optLong("uin") == bot.getId()) {
                    b.put("nick", bot.getNick());
                    break;
                }
            }
            s.save();
            for (SeikoPlugin plugin : BotRunnerService.INSTANCE.getSeikoPluginList()) {
                plugin.onBotGoLine(bot.getId());
            }
            bot.join();
            for (SeikoPlugin plugin : BotRunnerService.INSTANCE.getSeikoPluginList()) {
                plugin.onBotOffLine(bot.getId());
            }
            throw new RuntimeException("用户操作，BOT主动下线");
        } catch (Throwable e) {
            Message m = new Message();
            m.what = 1;
            Bundle b = new Bundle();
            b.putSerializable("exception", e);
            m.setData(b);
            dialogController.sendMessage(m);
        }
    }
}
