package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: JavaMethodInvoke
 * @author: kagg886
 * @description: $JAVA方法运行 存入变量 类名(全限定) 方法名 对象 参数列表$
 * @date: 2023/1/20 19:33
 * @version: 1.0
 */
public class JavaMethodInvoke extends Function.UnInterruptedFunction {

    public JavaMethodInvoke(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        //存入变量 类名(全限定) 方法名 对象 参数列表
        String putVar = args.get(0).toString();
        Class<?> clazz;
        try {
            clazz = Class.forName(args.get(1).toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (Method p : clazz.getDeclaredMethods()) {
            p.setAccessible(true);
            if (p.getName().equals(args.get(2).toString())) {
                System.out.println("找到方法");
                List<Object> paramList = new ArrayList<>();
                if (args.size() > 4) {
                    paramList.addAll(Collections.singletonList(args).subList(4, args.size()));
                }
                System.out.println("参数填充:" + paramList);
                try {
                    Object rtn = p.invoke(args.get(3), paramList.toArray());
                    if (rtn != null) {
                        runtime.getRuntimeObject().put(putVar, rtn);
                    } else {
                        runtime.getRuntimeObject().put(putVar, "null");
                    }
                    System.out.println("invoke完成!" + rtn.toString());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
