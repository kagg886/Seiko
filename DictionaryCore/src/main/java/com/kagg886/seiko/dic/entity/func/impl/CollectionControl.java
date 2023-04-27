package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import org.json.*;
import org.json.HTTP;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: CollectionControl
 * @author: kagg886
 * @description: 内建集合管理类
 * @date: 2023/2/1 12:09
 * @version: 1.0
 */
public abstract class CollectionControl extends Function.UnInterruptedFunction {
    public CollectionControl(int line, String code) {
        super(line, code);
    }


    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $取集合 存入的变量名 集合名/集合对象 键/索引$
     * @date: 2023/2/1 12:35
     * @version: 1.0
     */
    public static class Get extends CollectionControl {

        public Get(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(0).toString();
            Object o = args.get(1);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            String key = args.get(2).toString();
            if (o instanceof Map<?,?>) {
                runtime.getRuntimeObject().put(putVar, ((Map<?, ?>) o).get(key));
            } else if (o instanceof List<?>) {
                runtime.getRuntimeObject().put(putVar, ((List<?>) o).get(Integer.parseInt(key)));
            } else {
                throw new DictionaryOnRunningException("参数不是集合!");
            }

        }
    }


    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合长 存入变量 集合名/集合变量$
     * @date: 2023/2/1 12:35
     * @version: 1.0
     */
    public static class Length extends CollectionControl {

        public Length(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(0).toString();
            Object o = args.get(1);
            int size;
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o);
            }
            if (o instanceof Map<?, ?>) {
                size = ((Map<?, ?>) o).size();
            } else if (o instanceof List<?>) {
                size = ((List<?>) o).size();
            } else {
                throw new DictionaryOnRunningException("参数不是集合!");
            }
            runtime.getRuntimeObject().put(putVar, size);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合导入 集合变量名 字符串$
     * @date: 2023/2/1 12:35
     * @version: 1.0
     */
    public static class From extends CollectionControl implements ArgumentLimiter {

        public From(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String varName = args.get(0).toString();
            String val = args.get(1).toString();
            try {
                runtime.getRuntimeObject().put(varName, new JSONObject(val).toMap());
                return;
            } catch (Exception ignored) {
            }
            //XML.toJSONObject遇到array会置空
            try {
                runtime.getRuntimeObject().put(varName, new JSONArray(val).toList());
                return;
            } catch (Exception ignored) {
            }
            try {
                runtime.getRuntimeObject().put(varName, XML.toJSONObject(val).toMap());
                return;
            } catch (Exception ignored) {
            }
            try {
                runtime.getRuntimeObject().put(varName, HTTP.toJSONObject(val).toMap());
                return;
            } catch (Exception ignored) {
            }
            try {
                runtime.getRuntimeObject().put(varName, Cookie.toJSONObject(val).toMap());
            } catch (Exception ignored) {
            }

            throw new DictionaryOnRunningException(val + "不是集合!");
        }

        @Override
        public int getArgumentLength() {
            return 2;
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合转 集合名/集合变量 转入变量名$
     * @date: 2023/2/1 12:35
     * @version: 1.0
     */
    public static class To extends CollectionControl {
        public To(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            String varName = args.get(1).toString();
            if (o instanceof Map<?,?>) {
                runtime.getRuntimeObject().put(varName, new JSONObject(o).toString());
            } else if (o instanceof List<?>) {
                runtime.getRuntimeObject().put(varName,new JSONArray(o).toString());
            } else {
                throw new DictionaryOnRunningException("参数不是集合!");
            }
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合检验 集合名/集合对象 检验变量 存入变量$
     * @date: 2023/2/1 12:35
     * @version: 1.0
     */
    public static class CheckExists extends CollectionControl {
        public CheckExists(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            String varName = args.get(1).toString();
            String input = args.get(2).toString();
            if (o instanceof Map<?,?>) {
                runtime.getRuntimeObject().put(input, ((Map<?, ?>) o).containsKey(varName));
            } else if (o instanceof List<?>){
                runtime.getRuntimeObject().put(input, ((List<?>) o).size() > Integer.parseInt(varName));
            } else {
                throw new DictionaryOnRunningException("参数不是集合!");
            }
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合删除 集合名/集合对象 集合变量$
     * @date: 2023/2/1 12:20
     * @version: 1.0
     */
    public static class DelVar extends CollectionControl {
        public DelVar(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            String varName = args.get(1).toString();

            if (o instanceof Map<?,?>) {
                ((Map<?, ?>) o).remove(varName);
            } else if (o instanceof List<?>) {
                ((List<?>) o).remove(Integer.parseInt(varName));
            } else {
                throw new DictionaryOnRunningException("参数不是集合!");
            }
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合赋值 集合名/集合对象 集合变量 要赋予的值$
     * @date: 2023/2/1 12:10
     * @version: 1.0
     */
    public static class SetVar extends CollectionControl implements ArgumentLimiter {
        public SetVar(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object o = args.get(0);
            if (o instanceof String) {
                o = runtime.getRuntimeObject().get(o.toString());
            }
            String varName = args.get(1).toString();
            Object value = args.get(2);
            if (o instanceof Map<?,?>) {
                ((Map<String,Object>) o).put(varName, value);
            } else if (o instanceof List<?>) {
                ((List<Object>) o).add(Integer.parseInt(varName),value);
            } else {
                throw new DictionaryOnRunningException("参数不是集合!");
            }
        }


        @Override
        public int getArgumentLength() {
            return 3;
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合创建 变量名$
     * @date: 2023/2/1 12:10
     * @version: 1.0
     */
    public static class Create extends CollectionControl {
        public Create(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String var = args.get(0).toString();
            Map<String, Object> map = new HashMap<>();
            runtime.getRuntimeObject().put(var, map);
        }
    }

}
