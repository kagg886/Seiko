package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.DictionaryUtil;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: NewVar
 * @author: kagg886
 * @description: $赋值 变量名 值$
 * @date: 2023/1/28 21:49
 * @version: 1.0
 */
public class SetVar extends Function.UnInterruptedFunction {
    public SetVar(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        String name = args.get(0).toString();
        Object value = args.get(1);
        if (value instanceof String) {
            value = DictionaryUtil.cleanVariableCode((String) value, runtime);
        }
        runtime.getRuntimeObject().put(name, value);
    }
}
