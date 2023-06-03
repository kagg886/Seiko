package com.kagg886.seiko.fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.alibaba.fastjson.JSON;
import com.kagg886.seiko.BuildConfig;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.adapter.ProtocolUnitAdapter;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.util.ProtocolInjector;
import com.kagg886.seiko.util.ShareUtil;
import com.kagg886.seiko.util.TextUtils;
import com.kagg886.seiko.util.storage.JSONObjectStorage;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

import static com.kagg886.seiko.fragment.LoginFragment.protocols;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    @SuppressLint("DefaultLocale")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference s = findPreference("SeikoInfo");
        PackageInfo p;
        try {
            p = requireContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        s.setTitle(text(R.string.settings_version_title, p.versionName, BuildConfig.COMMIT_HASH));
        s.setSummary(text(R.string.settings_version_summary, p.getLongVersionCode()));

        s = findPreference("buildTime");
        s.setSummary(BuildConfig.BUILD_TIME);

        s = findPreference("MiraiInfo");
        s.setSummary(BuildConfig.MIRAI_VERSION);

        EditTextPreference sp = findPreference("maxLogNum");
        sp.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        sp.setOnPreferenceChangeListener(this);
        sp.setSummary(String.format("当前设置的值为: %s", sp.getSharedPreferences().getString("maxLogNum", "40")));

        s = findPreference("goGithub");
        s.setOnPreferenceClickListener(this);

        s = findPreference("mergeAllLogs");
        s.setOnPreferenceChangeListener(this);

        s = findPreference("protocolSetting");
        s.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        switch (preference.getKey()) {
            case "goGithub":
                ShareUtil.openUrlByBrowser("https://github.com/kagg886/Seiko");
                break;

            case "protocolSetting":
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(new ProtocolSettingsDialog().create());
                builder.create().show();
                break;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(@NonNull @NotNull Preference preference, Object newValue) {
        if (preference.getKey().equals("maxLogNum")) {
            if (TextUtils.isEmpty(((String) newValue))) {
                SnackBroadCast.sendBroadCast("至少输入一个值!");
                return false;
            }
            if (Integer.parseInt((String) newValue) > 0) {
                preference.setSummary(String.format("当前设置的值为:%s", newValue));
                SnackBroadCast.sendBroadCast("保存成功!");
                return true;
            }
            SnackBroadCast.sendBroadCast("不能为0和负数!");
            return false;
        }


        if (preference.getKey().equals("mergeAllLogs")) {
            if (Bot.getInstances().size() != 0) {
                SnackBroadCast.sendBroadCast("请下线所有bot后重试");
                return false;
            }
            return true;
        }

        return false;
    }

    private String text(@StringRes int s, Object... args) {
        return String.format(SeikoApplication.getSeikoApplicationContext().getText(s).toString(), args);
    }

    /**
     * 协议设置的活动
     *
     * @author kagg886
     * @date 2023/6/2 20:12
     **/
    public static class ProtocolSettingsDialog implements AdapterView.OnItemSelectedListener, View.OnClickListener {
        private Spinner spinner;
        private ListView listView;

        private Button success;

        private Button reset;

        private ProtocolUnitAdapter adapter;


        protected View create() {
            View root = LayoutInflater.from(SeikoApplication.getCurrentActivity()).inflate(R.layout.activity_protocol_fix, null);

            spinner = root.findViewById(R.id.activity_protocol_fix_select);
            listView = root.findViewById(R.id.activity_protocol_fix_layout);
            success = root.findViewById(R.id.activity_protocol_fix_success);
            reset = root.findViewById(R.id.activity_protocol_fix_reset);

            spinner.setAdapter(new ArrayAdapter<>(SeikoApplication.getSeikoApplicationContext(), android.R.layout.simple_list_item_1, protocols));
            spinner.setOnItemSelectedListener(this);

            adapter = new ProtocolUnitAdapter();
            adapter.setCurrentProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
            listView.setAdapter(adapter);

            success.setOnClickListener(this);
            reset.setOnClickListener(this);
            return root;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.valueOf(protocols[position]);
            adapter.setCurrentProtocol(protocol);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        public void onClick(View v) {
            BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.valueOf((String) spinner.getSelectedItem());
            JSONObjectStorage storage = JSONObjectStorage.obtain(Paths.get(SeikoApplication.getSeikoApplicationContext().getExternalFilesDir("config").toString(), "customProtocol.json").toFile().getAbsolutePath());

            if (Bot.getInstances().size() != 0) {
                SnackBroadCast.sendBroadCast("请下线所有bot后再操作");
                return;
            }
            switch (v.getId()) {
                case R.id.activity_protocol_fix_success:
                    ProtocolInjector injector = adapter.pack();
                    injector.inject(protocol);
                    storage.put((String) spinner.getSelectedItem(), JSON.toJSONString(injector));
                    SnackBroadCast.sendBroadCast("修改成功。");
                    break;
                case R.id.activity_protocol_fix_reset:
                    ProtocolInjector.restore(protocol);
                    storage.remove((String) spinner.getSelectedItem());
                    SnackBroadCast.sendBroadCast("还原成功。");
                    break;

            }
            storage.save();
            adapter.setCurrentProtocol(protocol); //触发视图更新
        }
    }

}