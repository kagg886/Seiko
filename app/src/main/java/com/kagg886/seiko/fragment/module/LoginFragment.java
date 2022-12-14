package com.kagg886.seiko.fragment.module;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.adapter.BotAdapter;
import com.kagg886.seiko.fragment.BaseFragment;
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
public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private BotAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new BotAdapter((MainActivity) getActivity());
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login, null);
        ListView lv = v.findViewById(R.id.fragment_login_view_list);
        FloatingActionButton btn = v.findViewById(R.id.fragment_login_widget_add);

        lv.setAdapter(adapter);
        btn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("添加账号");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_kv, null);
        builder.setView(view);
        builder.setPositiveButton("确定", (dialog, which) -> {
            TextInputLayout keyEdit = view.findViewById(R.id.dialog_editKey);
            TextInputLayout valueEdit = view.findViewById(R.id.dialog_editValue);
            String key = keyEdit.getEditText().getText().toString();
            String value = valueEdit.getEditText().getText().toString();

            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                return;
            }
            Long qq = 0L;
            try {
                qq = Long.parseLong(key);
            } catch (Exception e) {
                snack("请输入合法的qq号!");
                return;
            }

            if (Bot.getInstanceOrNull(qq) != null) {
                snack("请勿输入已存在的QQ");
            }
            JSONArrayStorage botList = JSONArrayStorage.obtain(getContext().getExternalFilesDir("config").getAbsolutePath() + "/botList.json");
            JSONObject account = new JSONObject();
            try {
                account.put("uin", qq);
                account.put("pass", value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            botList.put(account);
            botList.save();
            snack("添加成功!");
            adapter.notifyDataSetChanged();
        });

        builder.create().show();
    }
}
