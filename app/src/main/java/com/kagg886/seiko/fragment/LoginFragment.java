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
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.BotAdapter;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.util.storage.JSONArrayStorage;
import net.mamoe.mirai.Bot;

import java.util.Objects;

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

    public static final String[] protocols = {
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
            builder.setTitle(R.string.login_title_edit);
        } else {
            builder.setTitle(R.string.login_title_create);
        }
        View view = LayoutInflater.from(avt).inflate(R.layout.dialog_login, null);
        builder.setView(view);

        Spinner spinner = view.findViewById(R.id.dialog_protocol);
        spinner.setAdapter(new ArrayAdapter<>(avt, android.R.layout.simple_list_item_1, protocols));

        EditText keyEdit = view.findViewById(R.id.dialog_editKey);
        EditText valueEdit = view.findViewById(R.id.dialog_editValue);

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
            keyEdit.setText(account.getString("uin"));
            keyEdit.setEnabled(false);
            valueEdit.setText(account.getString("pass"));
            int i = 0;
            String protocol = account.getString("platform");
            if (protocol == null) {
                protocol = protocols[0];
            }
            while (!protocol.equals(protocols[i])) {
                i++;
            }
            spinner.setSelection(i);
            useQrScan.setChecked(account.getBoolean("useQRLogin"));
            if (useQrScan.isChecked()) {
                valueEdit.setVisibility(View.GONE);
            }
        }
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String key = keyEdit.getText().toString();
            String value = valueEdit.getText().toString();
            boolean useQRLogin = useQrScan.isChecked();

            if (TextUtils.isEmpty(key) || (TextUtils.isEmpty(value) && !useQRLogin)) {
                return;
            }
            Long qq;
            try {
                qq = Long.parseLong(key);
            } catch (Exception e) {
                SnackBroadCast.sendBroadCast(R.string.login_qq_not_vaild);
                return;
            }

            JSONArrayStorage botList = JSONArrayStorage.obtain(avt.getExternalFilesDir("config").getAbsolutePath() + "/botList.json");

            if (Bot.getInstanceOrNull(qq) != null && !isEdit) { //只有新增对话框中才需要检查新填写的qq和已存在列表是否相同
                SnackBroadCast.sendBroadCast(R.string.login_qq_exist);
                return;
            }

            for (int i = 0; i < botList.size(); i++) {
                if (Objects.equals(botList.getJSONObject(i).getLong("uin"), qq)) {
                    if (isEdit) {
                        botList.remove(i); //找到了就删掉!
                        break;
                    } else {
                        SnackBroadCast.sendBroadCast(R.string.login_qq_exist);
                        return;
                    }
                }
            }

            account.put("uin", qq);
            account.put("pass", value);
            account.put("useQRLogin", useQRLogin);
            account.put("platform", protocols[spinner.getSelectedItemPosition()]);
            botList.add(account);
            botList.save();
            if (isEdit) {
                SnackBroadCast.sendBroadCast(R.string.login_edit_success);
            } else {
                SnackBroadCast.sendBroadCast(R.string.login_create_success);
            }
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        return builder.create();
    }

    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        refresh.setRefreshing(false);
    }
}
