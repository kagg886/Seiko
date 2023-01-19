package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.DICPlugin;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: Read
 * @author: kagg886
 * @description: $读 存入变量名 文件路径 键 默认值$
 * @date: 2023/1/19 19:05
 * @version: 1.0
 */
public class Read extends Function.UnInterruptedFunction {

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
    protected void run(AbsRuntime<?> runtime, Object[] args) {
        String putVar = (String) args[0];
        String storage = DICPlugin.getDicData().resolve((String) args[1]).toFile().getAbsolutePath();
        String key = (String) args[2];
        String value;
        if (args.length == 3) {
            value = "null";
        } else {
            value = (String) args[3];
        }
        String get = get(storage, key, value);
        runtime.getRuntimeObject().put(putVar, get);
    }
}
