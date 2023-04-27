package com.kagg886.seiko.dic.entity.impl;

import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.util.DictionaryUtil;
import org.json.*;

import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.impl
 * @className: FastAssignment
 * @author: kagg886
 * @description: 代表快速赋值语句
 * @date: 2023/4/27 19:06
 * @version: 1.0
 */
public class FastAssignment extends DictionaryCode {
    private final String varName;

    private final String valueRef;

    public FastAssignment(int line, String code) {
        //STR<-123456
        //NUM<-6
        //PUM<-{"A","B"}
        //ARR<-[1,2,3]
        //CUS<-%A% %B%
        super(line, code);
        String[] val = code.split("<-",2);
        varName = val[0];
        valueRef = val[1];
    }

    public void addInRuntimeObject(AbsRuntime<?> runtime) {
        String val = DictionaryUtil.cleanVariableCode(valueRef,runtime);
        try {
            runtime.getRuntimeObject().put(varName, new JSONObject(val).toMap());
            return;
        } catch (Exception e) {
        }
        //XML.toJSONObject遇到array会置空
        try {
            runtime.getRuntimeObject().put(varName, new JSONArray(val).toList());
            return;
        } catch (Exception ignored) {
        }

        try {
            Map<?,?> map = XML.toJSONObject(val).toMap();
            if (map.size() == 0) {
                throw new Exception();
            }
            runtime.getRuntimeObject().put(varName, map);
            return;
        } catch (Exception ignored) {
        }

        runtime.getRuntimeObject().put(varName, val);
    }
}
