package com.kagg886.seiko.adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.bot.LoginThread;
import com.kagg886.seiko.fragment.module.LoginFragment;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import net.mamoe.mirai.Bot;
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
    private MainActivity avt;
    private JSONArrayStorage botList;

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
        SwitchCompat sw = v.findViewById(R.id.adapter_botitem_status);
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
                new LoginThread(avt, target, sw, nick).start();
            } else {
                Bot.getInstance(target.optLong("uin")).close();
                avt.snack(target.optLong("uin") + "已下线");
            }
        });

        v.setOnClickListener(v1 -> {
            JSONObject object = botList.optJSONObject(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(avt);
            builder.setTitle(String.valueOf(object.optLong("uin")));
            builder.setItems(new String[]{"导出设备信息", "登录配置", "导出日志", "删除BOT"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        IOUtil.quickShare(avt, new File(avt.getExternalFilesDir("bots") + "/" + object.optLong("uin") + "/device.json"), "*/*");
                        break;
                    case 1:
                        LoginFragment.editDialog(avt, this, true, object).show();
                        //TODO 修改密码等...
                        break;
                    case 2:
                        //TODO 添加导出日志的代码
                        break;
                    case 3:
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
