package com.kagg886.seiko.dic;

import android.content.Context;
import com.kagg886.seiko.dic.entity.Dictionary;

import java.io.File;
import java.util.ArrayList;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic
 * @className: DICList
 * @author: kagg886
 * @description: 保管了从存储里加载的伪代码文件
 * @date: 2023/1/9 19:33
 * @version: 1.0
 */
public class DICList extends ArrayList<Dictionary> {

    private Context ctx;

    public DICList(Context ctx) {
        this.ctx = ctx;
        refresh();
    }

    public void refresh() {
        clear();
        for (File p : ctx.getExternalFilesDir("dic").listFiles()) {
            add(new Dictionary(p));
        }
    }
}
