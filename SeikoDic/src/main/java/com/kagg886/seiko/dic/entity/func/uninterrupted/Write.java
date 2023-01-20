package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.DICPlugin;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: Write
 * @author: kagg886
 * @description: $写 文件路径 键 值$
 * @date: 2023/1/19 19:29
 * @version: 1.0
 */
public class Write extends Function.UnInterruptedFunction {

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
        String storage = DICPlugin.getDicData().resolve(args.get(0).toString()).toFile().getAbsolutePath();
        String key = args.get(1).toString();
        String value = args.get(2).toString();
        xx(storage, key, value);
    }
}
