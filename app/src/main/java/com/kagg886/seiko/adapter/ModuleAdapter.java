package com.kagg886.seiko.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.adapter
 * @className: ModuleAdapter
 * @author: kagg886
 * @description: 装载各种Fragment的适配器
 * @date: 2022/12/12 19:03
 * @version: 1.0
 */
public class ModuleAdapter extends FragmentPagerAdapter {
    private ArrayList<Structure> views = new ArrayList<>();

    public CharSequence getPageTitle(int i) {
        return this.views.get(i).name;
    }

    public ModuleAdapter(FragmentManager fragmentManager) {
        super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);
    }

    public void setViews(ArrayList<Structure> arrayList) {
        this.views = arrayList;
    }

    public Fragment getItem(int i) {
        return this.views.get(i).view;
    }

    public int getCount() {
        return this.views.size();
    }

    public static class Structure {
        public String name;
        public Fragment view;

        public Structure(String str, Fragment fragment) {
            this.name = str;
            this.view = fragment;
        }
    }
}