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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.fragment.module.LoginFragment;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import net.mamoe.mirai.Bot;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.OutputStream;
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
    private static ActivityResultLauncher<Intent> writeCall;
    private MainActivity avt;
    private JSONArrayStorage botList;

    public static long chooseUin;

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
                        File p = new File(avt.getExternalFilesDir("bots") + "/" + object.optLong("uin") + "/device.json");
                        if (!p.exists()) {
                            avt.snack("从未登陆过，无法获取到设备信息");
                            return;
                        }
                        IOUtil.quickShare(avt , p, "*/*");
                        break;
                    case 1:
                        LoginFragment.editDialog(avt, this, true, object).show();
                        break;
                    case 2:
                        chooseUin = object.optLong("uin");
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        intent.putExtra(Intent.EXTRA_TITLE, "log-" + object.optLong("uin")  + ".zip");
                        writeCall.launch(intent);
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
    
    public static void registerActivityResult(MainActivity avt) {
         writeCall = avt.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() == null) {
                return;
            }
            try {
                File p = new File(avt.getExternalFilesDir("bots") + "/" + BotAdapter.chooseUin + "/log");
                if (!p.isDirectory()) {
                    avt.snack("并没有日志，不需要导出");
                    return;
                }
                OutputStream stream = avt.getContentResolver().openOutputStream(result.getData().getData());
                ZipOutputStream output = new ZipOutputStream(stream);
                for (File a : p.listFiles()) {
                    output.putNextEntry(new ZipEntry(a.getName()));
                    output.write(IOUtil.loadByteFromFile(a.getAbsolutePath()));
                }
                output.close();
                stream.close();
                avt.snack("导出成功!");
            } catch (Exception e) {
                Log.w("DEBUG",e);
                avt.snack("导出失败!");
            }
        });
    }
}
