package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.entity.func.Function;
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

    private static final java.util.Random random = new java.util.Random();

    public Random(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        Integer a = null, b = null;
        String put = args.get(0).toString();

        //写A，B是[A,B)范围内随机整数
        //只写A是[0,A)范围内随机整数
        //什么都不写是[0,1)范围内的随机小数
        try {
            a = Integer.parseInt(args.get(1).toString());
        } catch (Exception ignored) {

        }

        try {
            b = Integer.parseInt(args.get(2).toString());
        } catch (Exception ignored) {
            if (a != null) {
                b = a;
                a = 0;
            }
        }

        if (a == null) {
            String format = "%f";
            try {
                format = args.get(1).toString();
            } catch (Exception ignored) {}
            runtime.getRuntimeObject().put(put,String.format(format,random.nextDouble()));
            return;
        }
        runtime.getRuntimeObject().put(put,random.nextInt(b-a) + a);

//        String put;
//        put = args.get(0).toString();
//        a = Integer.parseInt(args.get(1).toString());
//        try {
//            b = Integer.parseInt(args.get(2).toString());
//        } catch (NullPointerException e) {
//            b = a;
//            a = 0;
//        }
//        runtime.getRuntimeObject().put(put, random.nextInt(b) + a);
    }
}
