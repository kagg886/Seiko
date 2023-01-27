package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
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
