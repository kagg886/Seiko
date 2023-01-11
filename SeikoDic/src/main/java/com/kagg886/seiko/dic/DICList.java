package com.kagg886.seiko.dic;

import android.content.Context;
import android.content.Intent;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.util.IOUtil;

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
public class DICList extends ArrayList<DictionaryFile> {

    private final File dicFile;

    public DICList(Context context) {
        this.dicFile = context.getExternalFilesDir("dic");
        refresh(context);
    }

    public void refresh(Context context) {
        clear();
        for (File p : dicFile.listFiles()) {
            try {
                add(new DictionaryFile(p));
            } catch (Throwable e) {
                Intent broadcast = new Intent("DialogBroadCast");
                broadcast.putExtra("name", "加载伪代码文件:[" + p.getName() + "]时发生错误!");
                broadcast.putExtra("error", IOUtil.getException(e));
                context.sendBroadcast(broadcast);
            }
        }
    }
}
