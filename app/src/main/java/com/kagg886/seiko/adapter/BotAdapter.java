package com.kagg886.seiko.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.LogActivity;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.fragment.module.LoginFragment;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.Mirai;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.adapter
 * @className: BotAdapter
 * @author: kagg886
 * @description: 显示BOT列表的
 * @date: 2022/12/12 19:22
 * @version: 1.0
 */
public class BotAdapter extends BaseAdapter {
    private static ActivityResultLauncher<Intent> writeCall;
    private final MainActivity avt;
    private final JSONArrayStorage botList;

    @Override
    public int getCount() {
        return botList.length();
    }

    public BotAdapter(MainActivity a) {
        this.avt = a;
        botList = JSONArrayStorage.obtain(avt.getExternalFilesDir("config").getAbsolutePath() + "/botList.json");
    }

    @Override
    public Object getItem(int position) {
        return botList.opt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(int position, View a, ViewGroup b) {
        View v = LayoutInflater.from(avt).inflate(R.layout.adapter_botitem, null);
        ImageView imageView = v.findViewById(R.id.adapter_botitem_imgContent);
        TextView nick = v.findViewById(R.id.adapter_botitem_botqqnick);
        TextView qq = v.findViewById(R.id.adapter_botitem_botqqid);
        SwitchCompat sw = v.findViewById(R.id.adapter_dicitem_status);
        JSONObject target = botList.optJSONObject(position);

        nick.setText(target.optString("nick", "未登录"));
        qq.setText(String.valueOf(target.optLong("uin")));
        IOUtil.asyncHttp(avt, Jsoup.connect("https://q1.qlogo.cn/g?b=qq&nk=" + qq.getText().toString() + "&s=640").ignoreContentType(true).timeout(10000), new IOUtil.Response() {
            @Override
            public void onSuccess(byte[] byt) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(byt, 0, byt.length));
            }

            @Override
            public void onFailed(Throwable t) {
                imageView.setImageResource(R.drawable.ic_error);
            }
        });

        if (Bot.getInstanceOrNull(target.optLong("uin")) != null) { //若BOT实例存在则检测BOT是否在线
            sw.setChecked(Bot.getInstanceOrNull(target.optLong("uin")).isOnline());
        }

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }
            if (isChecked) {
                BotRunnerService.INSTANCE.login(target,nick,sw);
            } else {
                Bot bot = Bot.getInstance(target.optLong("uin"));
                for (SeikoPlugin plugin : BotRunnerService.INSTANCE.getSeikoPluginList()) {
                    plugin.onBotGoLine(bot.getId());
                }
                Bot.getInstance(target.optLong("uin")).close();
                //avt.snack(target.optLong("uin") + "已下线");
            }
        });

        v.setOnClickListener(v1 -> {
            JSONObject object = botList.optJSONObject(position);
            String uin = String.valueOf(object.optLong("uin"));
            AlertDialog.Builder builder = new AlertDialog.Builder(avt);
            builder.setTitle(uin);
            builder.setItems(new String[]{"导出设备信息", "登录配置", "查看日志", "删除BOT"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        File p = new File(avt.getExternalFilesDir("bots") + "/" + uin + "/device.json");
                        if (!p.exists()) {
                            avt.snack("从未登陆过，无法获取到设备信息");
                            return;
                        }
                        IOUtil.quickShare(avt, p, "*/*");
                        break;
                    case 1:
                        if (Bot.getInstanceOrNull(object.optLong("uin")) != null) {
                            avt.snack("请下线BOT然后再执行此操作!");
                            return;
                        }
                        LoginFragment.editDialog(avt, this, true, object).show();
                        break;
                    case 2:
                        Intent i = new Intent(avt, LogActivity.class);
                        i.putExtra("uin", uin);
                        avt.startActivity(i);
                        break;
                    case 3:
                        if (Bot.getInstanceOrNull(object.optLong("uin")) != null) {
                            avt.snack("请下线BOT然后再执行此操作!");
                            return;
                        }
                        botList.remove(position);
                        botList.save();
                        notifyDataSetChanged();
                        avt.snack("删除完毕");
                        break;
                }
            });
            builder.create().show();
        });
        return v;
    }
}
