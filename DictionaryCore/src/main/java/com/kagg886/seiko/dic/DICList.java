package com.kagg886.seiko.dic;

import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.model.DICParseResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

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

    private static final DICList DIC_LIST = new DICList();;


    public DICList() {
        refresh();
    }

    public static DICList getInstance() {
        return DIC_LIST;
    }

    public DICParseResult refresh() {
        clear();
        DICParseResult dicParseResult = new DICParseResult();
        for (File p : Objects.requireNonNull(DictionaryEnvironment.getInstance().getDicRoot().listFiles())) {
            if (p.isDirectory()) {
                continue;
            }
            DictionaryFile dictionaryFile = new DictionaryFile(p);

            // 放在这里可能不合适，每次刷新都要判断，暂时没有好的地方塞，这里目的是初始化jsonObject
            JSONObject jsonObject = DictionaryEnvironment.getInstance().getDicConfig().getJSONObject(dictionaryFile.getName());
            if(jsonObject == null) {
                jsonObject = new JSONObject();
            }

            try {
                dictionaryFile.parseDICCodeFile();
            } catch (Throwable e) {
                jsonObject.put("enabled", false);
                // 这里最好不返回异常，而是用bool指代是否刷新成功
                dicParseResult.success = false;
                dicParseResult.err.add(e);
            } finally {
                add(dictionaryFile);
                DictionaryEnvironment.getInstance().updateDICConfig(dictionaryFile.getName(), jsonObject);
            }
        }
        return dicParseResult;
    }
}
