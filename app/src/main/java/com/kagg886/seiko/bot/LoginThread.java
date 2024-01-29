package com.kagg886.seiko.bot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.network.BotAuthorizationException;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.nio.file.Paths;

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
    private final JSONObject botConfig;

    private final Bot bot;

    private final SwitchCompat sw;
    private final TextView nick;

    private final AlertDialog dialog;
    private final Handler dialogController = new Handler(Looper.getMainLooper()) { //Dialog控制的Handler

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 2: //展示对话框
                    dialog.show();
                    break;
                case 0:
                    SnackBroadCast.sendBroadCast(text(R.string.login_success, bot.getId()));
                    sw.setChecked(true);
                    nick.setText(bot.getNick());
                    dialog.dismiss();
                    break;
                case 1:
                    Throwable throwable = ((Throwable) msg.getData().getSerializable("exception"));
                    if (!(throwable instanceof NormalOffline)) {
                        if (throwable.getClass() == BotAuthorizationException.class) {
                            throwable = throwable.getCause();
                        }
                        if (SeikoApplication.globalConfig.getBoolean("badDeviceAutoDel", true)) {
                            if (throwable instanceof LoginFailedException) {
                                if (((LoginFailedException) throwable).getKillBot()) {
                                    //重置设备信息
                                    File file = Paths.get(SeikoApplication.getSeikoApplicationContext().getExternalFilesDir("bots").getAbsolutePath(),
                                            String.valueOf(bot.getId())).toFile();
                                    bot.getLogger().error("bot登录失败，自动清除设备信息");
                                    IOUtil.delFile(file, pathname -> {
                                        for (File t = pathname; !t.getAbsolutePath().equals("/"); t = t.getParentFile()) {
                                            if (t.getName().equals("log") && t.isDirectory()) {
                                                return false;
                                            }
                                        }
                                        return true;
                                    });
                                }
                            }
                        }
                        String error = throwable.getMessage() == null ? text(R.string.login_fail_reason_unknown) : text(R.string.login_fail_reason, throwable.getMessage());

                        if (error.startsWith("Bot is already closed and cannot relogin")) {
                            bot.getLogger().debug("登录时发现线程持有Bot过期，正在尝试重新创建线程...");
                            //重新设置Bot
                            BotRunnerService.INSTANCE.login(botConfig, nick,sw);
                            dialog.dismiss();
                            return;
                        }
                        DialogBroadCast.sendBroadCast(text(R.string.login_fail), error);
                        bot.getLogger().error("在Bot登录时发现异常: ", throwable);
                    }
                    sw.setChecked(false);
                    dialog.dismiss();
                    break;
            }
        }
    };

    public long getBot() {
        return bot.getId();
    }

    public SwitchCompat getSwitch() {
        return sw;
    }

    @SuppressLint("DefaultLocale")
    public LoginThread(JSONObject botConfig, SwitchCompat sw, TextView nick) {
        this.nick = nick;
        this.sw = sw;
        this.botConfig = botConfig;

        long uin = botConfig.getLong("uin");
        String pass = botConfig.getString("pass");

        String platform = botConfig.getString("platform");
        if (platform == null) {
            platform = "ANDROID_PHONE";
        }

        BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.valueOf(platform);

        dialog = new AlertDialog.Builder(SeikoApplication.getCurrentActivity())
                .setTitle(text(R.string.login_process, uin))
                .setCancelable(false)
                .setMessage("请稍等片刻")
                .create();
        BotLogConfiguration configuration = new BotLogConfiguration(uin);
        configuration.setProtocol(protocol);

        if (botConfig.getBoolean("useQRLogin")) {
            bot = BotFactory.INSTANCE.newBot(uin, BotAuthorization.byQRCode(), configuration);
        } else {
            bot = BotFactory.INSTANCE.newBot(uin, BotAuthorization.byPassword(pass), configuration);
        }
    }

    private void setDialogContent(String text) {
        Message message = new Message();
        message.what = 3;
        message.getData().putString("content", text);
        dialogController.sendMessage(message);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void run() {
        try {
            Context ctx = SeikoApplication.getSeikoApplicationContext();
            dialogController.sendEmptyMessage(2);
            setDialogContent(ctx.getString(R.string.login_process_message));
            bot.login();
            dialogController.sendEmptyMessage(0);
            JSONArrayStorage s = JSONArrayStorage.obtain(SeikoApplication.getSeikoApplicationContext().getExternalFilesDir("config").getAbsolutePath() + "/botList.json");
            for (int i = 0; i < s.size(); i++) {
                JSONObject b = s.getJSONObject(i);
                if (b.getLong("uin") == bot.getId()) {
                    b.put("nick", bot.getNick());
                    break;
                }
            }
            s.save();
            for (SeikoPlugin plugin : BotRunnerService.INSTANCE.getSeikoPluginList()) {
                try {
                    plugin.onBotGoLine(bot.getId());
                } catch (Throwable e) {
                    bot.getLogger().error("加载插件: " + plugin.getDescription().getName() + " (" + plugin.getDescription().getId() + ") 发生错误!", e);
                    SnackBroadCast.sendBroadCast("初始化: " + plugin.getDescription().getName() + " 时发生错误, 请前往bot日志查看。");
                }
                BotRunnerService.INSTANCE.getLastLoad().put(plugin.getDescription().getId(), bot.getId());
            }
            bot.join();
            SnackBroadCast.sendBroadCast(R.string.login_logout);
            throw new NormalOffline();
        } catch (Throwable e) {
            Message m = new Message();
            m.what = 1;
            Bundle b = new Bundle();
            b.putSerializable("exception", e);
            m.setData(b);
            dialogController.sendMessage(m);
        }
    }

    private String text(@StringRes int s, Object... args) {
        return String.format(SeikoApplication.getSeikoApplicationContext().getText(s).toString(), args);
    }

    private static class NormalOffline extends Throwable {

    }
}
