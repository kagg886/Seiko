package com.kagg886.seiko.dic.entity.func.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.util.IOUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: FileControl
 * @author: kagg886
 * @description: 文件操作函数
 * @date: 2023/3/2 21:51
 * @version: 1.0
 */
public abstract class FileControl extends Function.UnInterruptedFunction {

    public FileControl(int line, String code) {
        super(line, code);
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: ReadFile
     * @author: kagg886
     * @description: $删 文件(夹)路径$
     * @date: 2023/4/8 11:31
     * @version: 1.0
     */
    public static class DelFile extends Function.UnInterruptedFunction implements ArgumentLimiter {

        public DelFile(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            File f = DictionaryEnvironment.getInstance().getDicData().resolve(args.get(1).toString()).toFile();
            IOUtil.delFile(f);
        }

        @Override
        public int getArgumentLength() {
            return 1;
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: ReadFile
     * @author: kagg886
     * @description: $写文件 文件内容 文件路径$
     * @date: 2023/4/7 17:56
     * @version: 1.0
     */
    public static class WriteFile extends Function.UnInterruptedFunction implements ArgumentLimiter {

        public WriteFile(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            File f = DictionaryEnvironment.getInstance().getDicData().resolve(args.get(1).toString()).toFile();
            try {
                if (!f.exists()) {
                    f.createNewFile();
                }
                IOUtil.writeStringToFile(f.getAbsolutePath(),args.get(0).toString());
            } catch (IOException e) {
                throw new DictionaryOnRunningException("文件写入失败:" + e.getMessage());
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
     * @className: ReadFile
     * @author: kagg886
     * @description: $读文件 存入变量名 文件路径$
     * @date: 2023/4/7 17:30
     * @version: 1.0
     */
    public static class ReadFile extends Function.UnInterruptedFunction implements ArgumentLimiter {

        public ReadFile(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(0).toString();
            File f = DictionaryEnvironment.getInstance().getDicData().resolve(args.get(1).toString()).toFile();
            try {
                runtime.getRuntimeObject().put(putVar, IOUtil.loadStringFromFile(f.getAbsolutePath()));
            } catch (IOException e) {
                throw new DictionaryOnRunningException("文件访问失败:" + e.getMessage());
            }
        }

        @Override
        public int getArgumentLength() {
            return 2;
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Read
     * @author: kagg886
     * @description: $读 存入变量名 文件路径 键 默认值$
     * @date: 2023/1/19 19:05
     * @version: 1.0
     */
    public static class Read extends Function.UnInterruptedFunction {

        public Read(int line, String code) {
            super(line, code);
        }

        //祖传代码(具体可以追溯到2018年)，无需优化
        public static String get(String str, String str2, String str3) {
            File file = new File(str);
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(file));
            } catch (IOException e) {
            }
            return properties.getProperty(str2, str3);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String putVar = args.get(0).toString();
            String storage = DictionaryEnvironment.getInstance().getDicData().resolve(args.get(1).toString()).toFile().getAbsolutePath();
            String key = args.get(2).toString();
            String value;
            if (args.size() == 3) {
                value = "null";
            } else {
                value = args.get(3).toString();
            }
            String get = get(storage, key, value);
            runtime.getRuntimeObject().put(putVar, get);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: Write
     * @author: kagg886
     * @description: $写 文件路径 键 值$
     * @date: 2023/1/19 19:29
     * @version: 1.0
     */
    public static class Write extends Function.UnInterruptedFunction {

        public Write(int line, String code) {
            super(line, code);
        }

        //祖传代码(具体可以追溯到2018年)
        public static boolean xx(String str, String str2, String str3) {
            File file = new File(str);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(str));
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            properties.setProperty(str2, str3);
            try {
                properties.store(new FileOutputStream(str), null);
            } catch (FileNotFoundException e4) {
                e4.printStackTrace();
            } catch (IOException e5) {
                e5.printStackTrace();
            }
            return false;
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String storage = DictionaryEnvironment.getInstance().getDicData().resolve(args.get(0).toString()).toFile().getAbsolutePath();
            String key = args.get(1).toString();
            Object obj = args.get(2);
            String val;
            if (obj instanceof HashMap<?, ?> || obj instanceof ArrayList<?>) {
                val = JSON.toJSONString(obj);
            } else {
                val = obj.toString();
            }
            xx(storage, key, val);
        }
    }

}
