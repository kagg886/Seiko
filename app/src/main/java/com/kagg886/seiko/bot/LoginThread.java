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
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import kotlin.coroutines.Continuation;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthInfo;
import net.mamoe.mirai.auth.BotAuthResult;
import net.mamoe.mirai.auth.BotAuthSession;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

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
    private final Bot bot;
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
                    SnackBroadCast.sendBroadCast("Bot:" + bot.getId() + "登录成功");
                    nick.setText(bot.getNick());
                    dialog.dismiss();
                    break;
                case 1:
                    Throwable throwable = ((Throwable) msg.getData().getSerializable("exception"));
                    if (throwable instanceof LoginFailedException) {
                        if (((LoginFailedException) throwable).getKillBot()) {
                            //重置设备信息
                            File file = Paths.get(SeikoApplication.getSeikoApplicationContext().getExternalFilesDir("bot").getAbsolutePath(),
                                    String.valueOf(bot.getId())).toFile();
                            IOUtil.delFile(file);
                            bot.getLogger().error("bot登录失败，自动清除设备信息");
                        }
                    }
                    DialogBroadCast.sendBroadCast("登录失败", throwable.getMessage() + "\n完整信息请查看bot日志");
                    bot.getLogger().error("在Bot登录时发现异常:", throwable);
                    sw.setChecked(false);
                    dialog.dismiss();
                    break;
            }
        }
    };

    @SuppressLint("DefaultLocale")
    public LoginThread(JSONObject botConfig, SwitchCompat sw, TextView nick) {
        this.nick = nick;
        this.sw = sw;

        long uin = botConfig.optLong("uin");
        String pass = botConfig.optString("pass");
        BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.valueOf(botConfig.optString("platform", "ANDROID_PHONE"));

        //在此处配置QRLogin
        //botConfig.optBoolean("useQRLogin");

        dialog = new AlertDialog.Builder(SeikoApplication.getCurrentActivity()).setTitle("登录中...(" + uin + ")").setCancelable(false).setMessage("请稍等片刻...").create();
        BotLogConfiguration configuration = new BotLogConfiguration(uin);
        configuration.setProtocol(protocol);

        if (botConfig.optBoolean("useQRLogin")) {
            bot = BotFactory.INSTANCE.newBot(uin, (botAuthSession, botAuthInfo, continuation) -> botAuthSession.authByQRCode(continuation),configuration);
        } else {
            bot = BotFactory.INSTANCE.newBot(uin, pass, configuration);
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void run() {
        try {
            dialogController.sendEmptyMessage(2);
            bot.login();
            dialogController.sendEmptyMessage(0);
            JSONArrayStorage s = JSONArrayStorage.obtain(SeikoApplication.getSeikoApplicationContext().getExternalFilesDir("config").getAbsolutePath() + "/botList.json");
            for (int i = 0; i < s.length(); i++) {
                JSONObject b = s.optJSONObject(i);
                if (b.optLong("uin") == bot.getId()) {
                    b.put("nick", bot.getNick());
                    break;
                }
            }
            s.save();
            for (SeikoPlugin plugin : BotRunnerService.INSTANCE.getSeikoPluginList()) {
                try {
                    plugin.onBotGoLine(bot.getId());
                } catch (Throwable e) {
                    bot.getLogger().error("加载插件:" + plugin.getDescription().getName() + "(" + plugin.getDescription().getId() + ")发生错误!", e);
                    SnackBroadCast.sendBroadCast("初始化:" + plugin.getDescription().getName() + "时发生错误,请前往bot日志查看。");
                }
                BotRunnerService.INSTANCE.getLastLoad().put(plugin.getDescription().getId(), bot.getId());
            }
            bot.join();
            SnackBroadCast.sendBroadCast("用户操作，BOT主动下线");
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
