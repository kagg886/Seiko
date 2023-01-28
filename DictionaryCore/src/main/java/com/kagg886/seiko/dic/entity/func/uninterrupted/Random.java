package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: Random
 * @author: kagg886
 * @description: $随机数 存入变量名 最小值 最大值$
 * @date: 2023/1/28 21:55
 * @version: 1.0
 */
public class Random extends Function.UnInterruptedFunction {

    private static java.util.Random random = new java.util.Random();

    public Random(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        int a, b;
        String put;
        try {
            put = args.get(0).toString();
            a = Integer.parseInt(args.get(1).toString());
            b = Integer.parseInt(args.get(2).toString());
        } catch (Throwable e) {
            throw new DictionaryOnRunningException(String.format("运行词库方法时出现错误---%s(%s)", runtime.getFile().getName(), this), e);
        }
        runtime.getRuntimeObject().put(put, random.nextInt(b) + a);
    }
}
