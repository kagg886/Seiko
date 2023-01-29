package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: CheckExists
 * @author: kagg886
 * @description: $检验变量 要存入的变量 要检验的变量$
 * @date: 2023/1/29 9:03
 * @version: 1.0
 */
public class CheckExists extends Function.UnInterruptedFunction {

    public CheckExists(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        String in = args.get(0).toString();
        String out = args.get(1).toString();
        runtime.getRuntimeObject().put(in, String.valueOf(runtime.getRuntimeObject().containsKey(out)));
    }
}
