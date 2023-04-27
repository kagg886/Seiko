package com.kagg886.seiko.dic.entity.func.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: ArrayControl
 * @author: kagg886
 * @description: 数组控制。
 * @date: 2023/2/16 16:32
 * @version: 1.0
 */
public abstract class ArrayControl extends Function.UnInterruptedFunction implements Function.Deprecated {

    @Override
    public String getAdvice() {
        return "使用集合函数代替此命令";
    }

    public ArrayControl(int line, String code) {
        super(line, code);
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Length
     * @author: kagg886
     * @description: $数组转 数组名/数组变量 存入变量名$
     * @date: 2023/2/16 16:15
     * @version: 1.0
     */
    public static class To extends ArrayControl {

        public To(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            String putVar = args.get(1).toString();
            List<Object> objects = (List<Object>) o;
            runtime.getRuntimeObject().put(putVar, new JSONArray(objects).toString());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Length
     * @author: kagg886
     * @description: $数组长 数组名/数组变量 存入变量$
     * @date: 2023/2/16 15:59
     * @version: 1.0
     */
    public static class Length extends ArrayControl {

        public Length(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            List<?> objects = toList(o);
            String putVar = args.get(1).toString();
            runtime.getRuntimeObject().put(putVar, objects.size());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Get
     * @author: kagg886
     * @description: $取数组 数组名/数组变量 序号 存入变量$
     * @date: 2023/2/16 15:47
     * @version: 1.0
     */
    public static class Get extends ArrayControl {

        public Get(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            List<?> objects = toList(o);
            int idx = Integer.parseInt(args.get(1).toString());
            String putVar = args.get(2).toString();
            runtime.getRuntimeObject().put(putVar, objects.get(idx));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Put
     * @author: kagg886
     * @description: $数组赋值 数组名 添加的变量 序号(可选)$
     * @date: 2023/2/16 15:21
     * @version: 1.0
     */
    public static class Put extends ArrayControl {

        public Put(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            List<Object> objects = toList(o);
            if (args.size() == 3) {
                int idx = Integer.parseInt(args.get(2).toString());
                objects.remove(idx);
                objects.add(idx, args.get(1));
            } else {
                objects.add(args.get(1));
            }
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Del
     * @author: kagg886
     * @description: $数组删除 数组名/数组变量 序号$
     * @date: 2023/2/16 15:10
     * @version: 1.0
     */
    public static class Del extends ArrayControl {

        public Del(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            List<?> objects = toList(o);
            objects.remove(Integer.parseInt(args.get(1).toString()));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Import
     * @author: kagg886
     * @description: $数组导入 存入变量名 数组字符串(json格式)$
     * @date: 2023/2/16 14:45
     * @version: 1.0
     */
    public static class Import extends ArrayControl {

        public Import(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(0).toString();
            String json = args.get(1).toString();
            runtime.getRuntimeObject().put(putVar, JSON.parseObject(json, ArrayList.class));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Create
     * @author: kagg886
     * @description: $数组创建 数组名$
     * @date: 2023/2/16 14:31
     * @version: 1.0
     */
    public static class Create extends UnInterruptedFunction {

        public Create(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            //JSON内部使用ArrayList做维护。故使用此
            runtime.getRuntimeObject().put(name, new ArrayList<>());
        }
    }

    private static List<Object> toList(Object o) {
        if (!(o instanceof List<?>)) {
            throw new DictionaryOnRunningException("此对象不是数组!");
        }
        return (List<Object>) o;
    }
}
