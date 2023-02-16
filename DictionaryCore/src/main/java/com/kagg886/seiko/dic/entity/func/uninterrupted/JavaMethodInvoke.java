package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
        for (Method p : clazz.getMethods()) {
            if (p.getName().equals(args.get(2).toString()) && p.getParameterCount() == (args.size() - 4)) {
                List<Object> paramList = new ArrayList<>();
                if (args.size() > 4) {
                    paramList.addAll(args.subList(4, args.size()));
                }
                try {
                    Object rtn = p.invoke(args.get(3), paramList.toArray());
                    if (rtn != null) {
                        runtime.getRuntimeObject().put(putVar, rtn);
                    } else {
                        runtime.getRuntimeObject().put(putVar, "null");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
