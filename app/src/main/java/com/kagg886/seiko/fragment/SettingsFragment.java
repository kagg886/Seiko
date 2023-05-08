package com.kagg886.seiko.fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.kagg886.seiko.BuildConfig;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.util.ShareUtil;
import com.kagg886.seiko.util.TextUtils;
import net.mamoe.mirai.Bot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    @SuppressLint("DefaultLocale")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference s = findPreference("SeikoInfo");
        PackageInfo p = null;
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


    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("goGithub")) {
            ShareUtil.openUrlByBrowser("https://github.com/kagg886/Seiko");
            return true;
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
}