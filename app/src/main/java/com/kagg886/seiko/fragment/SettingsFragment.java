package com.kagg886.seiko.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.kagg886.seiko.BuildConfig;
import com.kagg886.seiko.R;
import com.kagg886.seiko.event.SnackBroadCast;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference s = findPreference("SeikoInfo");
        PackageInfo p = null;
        try {
            p = getContext().getPackageManager().getPackageInfo("com.kagg886.seiko", 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        s.setTitle("当前版本:" + p.versionName);
        s.setSummary("版本号:" + p.getLongVersionCode());

        s = findPreference("buildTime");
        s.setTitle("构建日期");
        s.setSummary(BuildConfig.BUILD_TIME);

        s = findPreference("MiraiInfo");
        s.setSummary(BuildConfig.MIRAI_VERSION);

        s = findPreference("cleanCache");

        StringBuilder str = new StringBuilder("已发现");
        int total = 0;

        File f = getContext().getExternalFilesDir("bots");
        for (File bot : f.listFiles()) {
            f = new File(bot.getAbsolutePath() + "/log");
            if (!f.isDirectory()) {
                continue;
            }
            for (File log : f.listFiles()) {
                total += log.length();
            }
        }
        appends: {
            if ((total / 1024) < 1) {
                str.append(total).append("Byte");
                break appends;
            }
            if ((total / 1024 / 1024) < 1) {
                str.append(String.format("%.2f", total / 1024.0)).append("KB");
                break appends;
            }
            str.append(String.format("%.2f", total / 1024.0 / 1024.0)).append("MB");
        }

        s.setSummary(str.toString());
        s.setOnPreferenceClickListener(this);

        EditTextPreference sp = findPreference("maxLogNum");
        sp.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        sp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull @NotNull Preference preference, Object newValue) {
                if (Integer.parseInt((String) newValue) > 0) {
                    sp.setSummary(String.format("当前设置的值为:%s", newValue));
                    SnackBroadCast.sendBroadCast(getContext(), "保存成功!");
                    return true;
                }
                SnackBroadCast.sendBroadCast(getContext(), "不能为0和负数!");
                return false;
            }
        });
        sp.setSummary(String.format("当前设置的值为:%s", sp.getSharedPreferences().getString("maxLogNum", "40")));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("cleanCache")) {
            File f = getContext().getExternalFilesDir("bots");
            for (File bot : f.listFiles()) {
                f = new File(bot.getAbsolutePath() + "/log");
                if (!f.isDirectory()) {
                    continue;
                }
                for (File log : f.listFiles()) {
                    log.delete();
                }
                f.delete();
            }
            SnackBroadCast.sendBroadCast(getContext(), "清理完毕(๑′ᴗ‵๑)");
            preference.setSummary("已发现0Byte");
        }
        return false;
    }
}