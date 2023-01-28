package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: DelVar
 * @author: kagg886
 * @description: $删除 变量名$
 * @date: 2023/1/28 21:54
 * @version: 1.0
 */
public class DelVar extends Function.UnInterruptedFunction {

    public DelVar(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        String name = args.get(0).toString();
        runtime.getRuntimeObject().remove(name);
    }
}
