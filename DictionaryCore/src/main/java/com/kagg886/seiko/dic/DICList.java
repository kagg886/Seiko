package com.kagg886.seiko.dic;

import com.kagg886.seiko.dic.entity.DictionaryFile;

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

    private static final DICList DIC_LIST = new DICList();

    public DICList() {
        refresh();
    }

    public static DICList getInstance() {
        return DIC_LIST;
    }

    public void refresh() {
        clear();
        for (File p : DictionaryEnvironment.getInstance().getDicRoot().listFiles()) {
            try {
                add(new DictionaryFile(p));
            } catch (Throwable e) {
                DictionaryEnvironment.getInstance().getErrorListener().onError(p, e);
            }
        }
    }
}
