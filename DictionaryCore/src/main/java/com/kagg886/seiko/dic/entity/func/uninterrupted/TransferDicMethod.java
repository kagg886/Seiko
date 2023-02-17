package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.session.impl.FunctionRuntime;

import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: TransferDicMethod
 * @author: kagg886
 * @description: $调用 词库方法名$
 * @date: 2023/2/17 10:39
 * @version: 1.0
 */
public class TransferDicMethod extends Function.UnInterruptedFunction {

    public TransferDicMethod(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        String dicMethod = args.get(0).toString();
        FunctionRuntime dicRun = new FunctionRuntime(runtime.getFile(), runtime);
        dicRun.invoke(dicMethod);
    }
}
