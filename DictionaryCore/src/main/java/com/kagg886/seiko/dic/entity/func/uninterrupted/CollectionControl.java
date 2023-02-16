package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import org.json.Cookie;
import org.json.HTTP;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;
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
     * @description: $取集合 存入的变量名 集合名 键$
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
            String name = args.get(1).toString();
            String key = args.get(2).toString();
            Object o = runtime.getRuntimeObject().get(name);
            if (!(o instanceof HashMap)) {
                throw new DictionaryOnRunningException(String.format("此变量不是集合，无法按照集合方法操作  %s:%s", runtime.getFile().getName(), this));
            }
            runtime.getRuntimeObject().put(putVar, ((HashMap<?, ?>) o).get(key));
        }
    }


    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合长 存入变量 集合名$
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
            String colName = args.get(1).toString();
            runtime.getRuntimeObject().put(putVar, ((HashMap) runtime.getRuntimeObject().get(colName)).size());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合导入 存入的变量 字符串$
     * @date: 2023/2/1 12:35
     * @version: 1.0
     */
    public static class From extends CollectionControl {

        public From(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String varName = args.get(0).toString();
            StringBuilder val = new StringBuilder();
            int i = 0;
            while (++i < args.size()) {
                val.append(" ");
                val.append(args.get(i).toString());
            }
            try {
                runtime.getRuntimeObject().put(varName, new JSONObject(val.substring(1)).toMap());
                return;
            } catch (Exception e) {
            }
            try {
                runtime.getRuntimeObject().put(varName, XML.toJSONObject(val.substring(1)).toMap());
                return;
            } catch (Exception e) {
            }
            try {
                runtime.getRuntimeObject().put(varName, HTTP.toJSONObject(val.substring(1)).toMap());
                return;
            } catch (Exception e) {
            }
            try {
                runtime.getRuntimeObject().put(varName, Cookie.toJSONObject(val.substring(1)).toMap());
                return;
            } catch (Exception e) {
            }
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合转 集合名 转入变量 JSON/JAVA$
     * @date: 2023/2/1 12:35
     * @version: 1.0
     */
    public static class To extends CollectionControl {
        public To(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            String varName = args.get(1).toString();
            String input = args.get(2).toString();
            Object o = runtime.getRuntimeObject().get(name);
            if (!(o instanceof HashMap)) {
                throw new DictionaryOnRunningException(String.format("此变量不是集合，无法按照集合方法操作  %s:%s", runtime.getFile().getName(), this));
            }
            HashMap<String, Object> map = (HashMap<String, Object>) o;
            if (input.equals("JSON")) {
                runtime.getRuntimeObject().put(varName, new JSONObject(map).toString());
            }
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合检验 集合名 检验变量 存入变量$
     * @date: 2023/2/1 12:35
     * @version: 1.0
     */
    public static class CheckExists extends CollectionControl {
        public CheckExists(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            String varName = args.get(1).toString();
            String input = args.get(2).toString();
            Object o = runtime.getRuntimeObject().get(name);
            if (!(o instanceof HashMap)) {
                throw new DictionaryOnRunningException(String.format("此变量不是集合，无法按照集合方法操作  %s:%s", runtime.getFile().getName(), this));
            }
            HashMap<String, Object> map = (HashMap<String, Object>) o;
            runtime.getRuntimeObject().put(input, map.containsKey(varName));
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合删除 集合名 集合变量$
     * @date: 2023/2/1 12:20
     * @version: 1.0
     */
    public static class DelVar extends CollectionControl {
        public DelVar(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            String varName = args.get(1).toString();
            Object o = runtime.getRuntimeObject().get(name);
            if (!(o instanceof HashMap)) {
                throw new DictionaryOnRunningException(String.format("此变量不是集合，无法按照集合方法操作  %s:%s", runtime.getFile().getName(), this));
            }
            HashMap<String, Object> map = (HashMap<String, Object>) o;
            map.remove(varName);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CollectionControl
     * @author: kagg886
     * @description: $集合赋值 集合名 集合变量 要赋予的值$
     * @date: 2023/2/1 12:10
     * @version: 1.0
     */
    public static class SetVar extends CollectionControl {
        public SetVar(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            String varName = args.get(1).toString();
            Object value = args.get(2);
            Object o = runtime.getRuntimeObject().get(name);
            if (!(o instanceof HashMap)) {
                throw new DictionaryOnRunningException(String.format("此变量不是集合，无法按照集合方法操作  %s:%s", runtime.getFile().getName(), this));
            }
            HashMap<String, Object> map = (HashMap<String, Object>) o;
            map.put(varName, value);
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
            HashMap<String, Object> map = new HashMap<>();
            runtime.getRuntimeObject().put(var, map);
        }
    }

}
