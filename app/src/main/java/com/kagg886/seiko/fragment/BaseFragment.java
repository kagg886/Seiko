package com.kagg886.seiko.fragment;

import androidx.fragment.app.Fragment;
import com.kagg886.seiko.activity.MainActivity;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.fragment
 * @className: BaseFragment
 * @author: kagg886
 * @description: 基本的fragment类，提供了一些基本的配置
 * @date: 2022/12/12 19:12
 * @version: 1.0
 */
public class BaseFragment extends Fragment {

    public void snack(String text) {
        ((MainActivity)getActivity()).snake(text);
    }
}
