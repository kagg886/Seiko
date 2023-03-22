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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

        nick.setText(target.optString("nick", "未登录"));
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
                    StringBuilder builder = new StringBuilder();
                    builder.append("程序发现Mirai内建Bot存储库并没有这个bot号:" + target.optLong("uin"));
                    builder.append("\nMirai内建Bot存储库列表:" + Bot.getInstances());

                    List<Long> nativeBots = new ArrayList<>();
                    for (int i = 0; i < botList.length(); i++) {
                        nativeBots.add(botList.optJSONObject(i).optLong("uin"));
                    }

                    builder.append("\nSeiko内部维护的Bot列表:" + nativeBots);
                    builder.append("\n若您遇到了这个弹窗，证明在几天之前有人触发了这个bug并且闪退。请前往Seiko的仓库提issue并截图此页面。");

                    DialogBroadCast.sendBroadCast("小小的警告", builder.toString());
                }
                //SnackBroadCast.sendBroadCast(avt,target.optLong("uin") + "已下线");
            }
        });

        v.setOnClickListener(v1 -> {
            JSONObject object = botList.optJSONObject(position);
            String uin = String.valueOf(object.optLong("uin"));
            AlertDialog.Builder builder = new AlertDialog.Builder(avt);
            builder.setTitle(uin);
            builder.setItems(new String[]{"导出登录信息", "登录配置", "查看日志", "删除BOT"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        File p = new File(avt.getExternalFilesDir("bots") + "/" + uin);
                        if (!new File(p.getAbsolutePath(),"cache").exists()) {
                            SnackBroadCast.sendBroadCast("从未登陆过，无法获取到登录信息");
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
                            SnackBroadCast.sendBroadCast("导出信息失败...");
                        }
                        ShareUtil.quickShare(avt, zipFile, "*/*");
                        break;
                    case 1:
                        if (Bot.getInstanceOrNull(object.optLong("uin")) != null) {
                            SnackBroadCast.sendBroadCast("请下线BOT然后再执行此操作!");
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
                            SnackBroadCast.sendBroadCast("请下线BOT然后再执行此操作!");
                            return;
                        }
                        botList.remove(position);
                        botList.save();
                        notifyDataSetChanged();
                        SnackBroadCast.sendBroadCast("删除完毕");
                        break;
                }
            });
            builder.create().show();
        });
        return v;
    }
}
