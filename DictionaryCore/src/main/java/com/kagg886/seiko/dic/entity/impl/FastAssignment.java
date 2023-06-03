package com.kagg886.seiko.dic.entity.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.util.DictionaryUtil;

import java.util.HashMap;
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

        //FUN<-$读 RTN A.txt key value$
        super(line, code);
        String[] val = code.split("<-",2);
        varName = val[0];
        valueRef = val[1];
    }

    public void addInRuntimeObject(AbsRuntime<?> runtime) {
        if (valueRef.startsWith("$") && valueRef.endsWith("$")) { //方法返回值注入模式
            try {
                Function method = Function.parseFunction(valueRef,getLine());
                method.invoke(runtime);
                Object rtn = runtime.getRuntimeObject().get("RTN");
                runtime.getRuntimeObject().put(varName,rtn);
                runtime.getRuntimeObject().remove("RTN");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return;
        }

        //纯文本赋值模式
        try {
            runtime.getRuntimeObject().put(varName,JSON.parseObject(valueRef));
            return;
        } catch (Exception ignored) {

        }
        runtime.getRuntimeObject().put(varName, DictionaryUtil.cleanVariableCode(valueRef,runtime));
    }
}
