package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;
import org.json.JSONArray;

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
public abstract class ArrayControl extends Function.UnInterruptedFunction {

    public ArrayControl(int line, String code) {
        super(line, code);
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Length
     * @author: kagg886
     * @description: $数组转 数组名 存入变量$
     * @date: 2023/2/16 16:15
     * @version: 1.0
     */
    public static class To extends ArrayControl {

        public To(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(1).toString();
            String name = args.get(0).toString();
            List<Object> objects = (List<Object>) runtime.getRuntimeObject().get(name);
            runtime.getRuntimeObject().put(putVar, new JSONArray(objects).toString());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Length
     * @author: kagg886
     * @description: $数组长 数组名 存入变量$
     * @date: 2023/2/16 15:59
     * @version: 1.0
     */
    public static class Length extends ArrayControl {

        public Length(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            List<Object> objects = (List<Object>) runtime.getRuntimeObject().get(name);
            String putVar = args.get(1).toString();
            runtime.getRuntimeObject().put(putVar, objects.size());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Get
     * @author: kagg886
     * @description: $取数组 数组名 序号 存入变量$
     * @date: 2023/2/16 15:47
     * @version: 1.0
     */
    public static class Get extends ArrayControl {

        public Get(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            List<Object> objects = (List<Object>) runtime.getRuntimeObject().get(name);
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
            String putVar = args.get(0).toString();
            List<Object> objects = (List<Object>) runtime.getRuntimeObject().get(putVar);
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
     * @description: $数组删除 数组名 序号$
     * @date: 2023/2/16 15:10
     * @version: 1.0
     */
    public static class Del extends ArrayControl {

        public Del(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(0).toString();
            List<Object> objects = (List<Object>) runtime.getRuntimeObject().get(putVar);
            objects.remove(Integer.parseInt(args.get(1).toString()));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Import
     * @author: kagg886
     * @description: $数组导入 存入变量 数组字符串(json格式)$
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
            String json = args.get(0).toString();
            runtime.getRuntimeObject().put(putVar, new JSONArray(json).toList());
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
    public static class Create extends ArrayControl {

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
}
