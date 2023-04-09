package com.kagg886.seiko.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.LogActivity;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.fragment.LoginFragment;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.ShareUtil;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import net.mamoe.mirai.Bot;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

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
    private final MainActivity avt;
    private final JSONArrayStorage botList;

    public BotAdapter(MainActivity a) {
        this.avt = a;
        botList = JSONArrayStorage.obtain(avt.getExternalFilesDir("config").getAbsolutePath() + "/botList.json");
    }

    @Override
    public int getCount() {
        return botList.length();
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

        nick.setText(target.optString("nick", text(R.string.bot_list_not_login)));
        qq.setText(String.valueOf(target.optLong("uin")));

        IOUtil.asyncHttp(Jsoup.connect("https://q1.qlogo.cn/g?b=qq&nk=" + qq.getText().toString() + "&s=640"), new IOUtil.Response() {
            @Override
            public void onSuccess(byte[] byt) {
                avt.runOnUiThread(() -> imageView.setImageBitmap(BitmapFactory.decodeByteArray(byt, 0, byt.length)));
            }

            @Override
            public void onFailed(IOException e) {
                avt.runOnUiThread(() -> imageView.setImageResource(R.drawable.ic_error));
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
                BotRunnerService.INSTANCE.login(target, nick, sw);
            } else {
                try {
                    Bot.getInstance(target.optLong("uin")).close();
                } catch (NoSuchElementException e) {
                    //TODO 抓bug用，解决了就删除
                    List<Long> nativeBots = new ArrayList<>();
                    for (int i = 0; i < botList.length(); i++) {
                        nativeBots.add(botList.optJSONObject(i).optLong("uin"));
                    }

                    DialogBroadCast.sendBroadCast(text(R.string.bot_list_no_such_bot_title),
                            text(R.string.bot_list_no_such_bot, target.optLong("uin"), Bot.getInstances(), nativeBots));
                }
                //SnackBroadCast.sendBroadCast(avt,target.optLong("uin") + "已下线");
            }
        });

        v.setOnClickListener(v1 -> {
            JSONObject object = botList.optJSONObject(position);
            String uin = String.valueOf(object.optLong("uin"));
            AlertDialog.Builder builder = new AlertDialog.Builder(avt);
            builder.setTitle(uin);
            builder.setItems(new String[] { text(R.string.bot_list_action_export), text(R.string.bot_list_action_edit), text(R.string.bot_list_action_log), text(R.string.bot_list_action_delete)}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        File p = new File(avt.getExternalFilesDir("bots") + "/" + uin);
                        if (!new File(p.getAbsolutePath(),"cache").exists()) {
                            SnackBroadCast.sendBroadCast(R.string.bot_list_action_export_never_login);
                            return;
                        }
                        File config = new File(p, "loginTemplate.yml");
                        if (config.exists()) {
                            config.delete();
                        }
                        try {
                            config.createNewFile();
                            String template = String.format(IOUtil.loadStringFromStream(avt.getAssets().open("loginTemplate.yml")),
                                    uin,
                                    object.optString("pass"),
                                    object.optString("platform"));
                            IOUtil.writeStringToFile(config.getAbsolutePath(),template);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        File zipFile = new File(avt.getExternalFilesDir("tmp"), UUID.randomUUID().toString().replace("-", "") + ".zip");

                        try {
                            IOUtil.zipFile(p, zipFile);
                        } catch (IOException e) {
                            Log.w("BotAdapter", e);
                            SnackBroadCast.sendBroadCast(R.string.bot_list_action_export_fail);
                        }
                        ShareUtil.quickShare(avt, zipFile, "*/*");
                        break;
                    case 1:
                        if (Bot.getInstanceOrNull(object.optLong("uin")) != null) {
                            SnackBroadCast.sendBroadCast(R.string.bot_list_action_edit_online);
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
                            SnackBroadCast.sendBroadCast(R.string.bot_list_action_delete_online);
                            return;
                        }
                        botList.remove(position);
                        botList.save();
                        notifyDataSetChanged();
                        SnackBroadCast.sendBroadCast(R.string.bot_list_action_delete_success);
                        break;
                }
            });
            builder.create().show();
        });
        return v;
    }

    private String text(@StringRes int s, Object... args) {
        return String.format(avt.getText(s).toString(), args);
    }
}
