package com.kagg886.seiko.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.BotAdapter;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import net.mamoe.mirai.Bot;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.fragment.module
 * @className: LoginFragment
 * @author: kagg886
 * @description: 提供登录的UI
 * @date: 2022/12/12 19:14
 * @version: 1.0
 */
public class LoginFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private BotAdapter adapter;

    private SwipeRefreshLayout refresh;

    private static final String[] protocols = {
            "ANDROID_PHONE",
            "ANDROID_PAD",
            "ANDROID_WATCH",
            "IPAD",
            "MACOS"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new BotAdapter((MainActivity) getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_login, null);

        refresh = v.findViewById(R.id.fragment_login_view_refresh);
        refresh.setOnRefreshListener(this);
        ListView lv = v.findViewById(R.id.fragment_login_view_list);
        FloatingActionButton btn = v.findViewById(R.id.fragment_login_widget_add);

        lv.setAdapter(adapter);
        btn.setOnClickListener(this);
        btn.setOnLongClickListener(v1 -> {
            getActivity().finish();
            return false;
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        editDialog(((MainActivity) getActivity()), adapter, false, new JSONObject()).show();
    }

    public static AlertDialog editDialog(MainActivity avt, BaseAdapter adapter, boolean isEdit, JSONObject account) {
        AlertDialog.Builder builder = new AlertDialog.Builder(avt);
        if (isEdit) {
            builder.setTitle("编辑账号");
        } else {
            builder.setTitle("添加账号");
        }
        View view = LayoutInflater.from(avt).inflate(R.layout.dialog_kv, null);
        builder.setView(view);

        Spinner spinner = view.findViewById(R.id.dialog_protocol);
        spinner.setAdapter(new ArrayAdapter<>(avt, android.R.layout.simple_list_item_1, protocols));

        TextInputLayout keyEdit = view.findViewById(R.id.dialog_editKey);
        TextInputLayout valueEdit = view.findViewById(R.id.dialog_editValue);

        CheckBox useQrScan = view.findViewById(R.id.dialog_useQRScan);
        useQrScan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }
            if (isChecked) {
                valueEdit.setVisibility(View.GONE);
            } else {
                valueEdit.setVisibility(View.VISIBLE);
            }
        });

        if (isEdit) {
            keyEdit.getEditText().setText(account.optString("uin"));
            keyEdit.getEditText().setEnabled(false);
            valueEdit.getEditText().setText(account.optString("pass"));
            int i = 0;
            while (!account.optString("platform", protocols[0]).equals(protocols[i])) {
                i++;
            }
            spinner.setSelection(i);
            useQrScan.setChecked(account.optBoolean("useQRLogin"));
            if (useQrScan.isChecked()) {
                valueEdit.setVisibility(View.GONE);
            }
        }
        builder.setPositiveButton("确定", (dialog, which) -> {
            String key = keyEdit.getEditText().getText().toString();
            String value = valueEdit.getEditText().getText().toString();
            boolean useQRLogin = useQrScan.isChecked();

            if (TextUtils.isEmpty(key) || (TextUtils.isEmpty(value) && !useQRLogin)) {
                return;
            }
            Long qq;
            try {
                qq = Long.parseLong(key);
            } catch (Exception e) {
                SnackBroadCast.sendBroadCast("请输入合法的qq号!");
                return;
            }

            JSONArrayStorage botList = JSONArrayStorage.obtain(avt.getExternalFilesDir("config").getAbsolutePath() + "/botList.json");

            if (Bot.getInstanceOrNull(qq) != null && !isEdit) { //只有新增对话框中才需要检查新填写的qq和已存在列表是否相同
                SnackBroadCast.sendBroadCast("请勿输入已存在的QQ");
                return;
            }

            for (int i = 0; i < botList.length(); i++) {
                if (botList.optJSONObject(i).optLong("uin") == qq) {
                    if (isEdit) {
                        botList.remove(i); //找到了就删掉!
                        break;
                    } else {
                        SnackBroadCast.sendBroadCast("请勿输入已存在的QQ");
                        return;
                    }
                }
            }

            try {
                account.put("uin", qq);
                account.put("pass", value);
                account.put("useQRLogin",useQRLogin);
                account.put("platform", protocols[spinner.getSelectedItemPosition()]);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            botList.put(account);
            botList.save();
            if (isEdit) {
                SnackBroadCast.sendBroadCast("修改成功!");
            } else {
                SnackBroadCast.sendBroadCast("添加成功!");
            }
            adapter.notifyDataSetChanged();
        });
        return builder.create();
    }

    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        refresh.setRefreshing(false);
    }
}
