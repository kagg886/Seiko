package com.kagg886.seiko.fragment.module;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.kagg886.seiko.BuildConfig;
import com.kagg886.seiko.R;
import com.kagg886.seiko.activity.MainActivity;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference s = findPreference("verInfo");
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
                str.append(String.format("%.2f",total / 1024.0)).append("KB");
                break appends;
            }
            str.append(String.format("%.2f",total / 1024.0 / 1024.0)).append("MB");
        }

        s.setSummary(str.toString());
        s.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "cleanCache":
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
                ((MainActivity)getActivity()).snack("清理完毕(๑′ᴗ‵๑)");
                findPreference("cleanCache").setSummary("已发现0Byte");
                break;
        }
        return false;
    }
}