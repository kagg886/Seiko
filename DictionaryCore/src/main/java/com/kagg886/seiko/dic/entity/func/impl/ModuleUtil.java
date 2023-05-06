package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.session.impl.FunctionRuntime;
import com.kagg886.seiko.dic.session.impl.LifeCycleRuntime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.impl
 * @className: ModuleUtil
 * @author: kagg886
 * @description: 动态词库装载函数
 * @date: 2023/4/23 21:17
 * @version: 1.0
 */
public abstract class ModuleUtil extends Function.UnInterruptedFunction {

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.impl
     * @className: ModuleUtil
     * @author: kagg886
     * @description: $子文件调用 模块名 模拟发送的信息 继承上下文?(真/假)$
     * @date: 2023/4/23 21:33
     * @version: 1.0
     */
    public static class Caller extends Function.UnInterruptedFunction {


        public Caller(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            DictionaryFile f = runtime.getFile().getSubFile().get(args.get(0).toString());
            String method = args.get(1).toString();

            FunctionRuntime runtime1 = new FunctionRuntime(f, runtime);

            if (args.size() >= 3) {
                runtime1.setProxyClass(runtime.getProxyClass());
            }

            runtime1.invoke(method);

        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.impl
     * @className: ModuleUtil
     * @author: kagg886
     * @description: $卸载 模块名$
     * @date: 2023/4/23 21:33
     * @version: 1.0
     */
    public static class Delete extends Function.UnInterruptedFunction {


        public Delete(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            DictionaryFile df = runtime.getFile().getSubFile()
                    .get(name);
            df.notifyLifeCycle(LifeCycleRuntime.LifeCycle.DESTROY);
            runtime.getFile().getSubFile()
                    .remove(name);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.impl
     * @className: ModuleUtil
     * @author: kagg886
     * @description: $装载 伪代码文件 模块名(可选)$
     * @date: 2023/4/23 21:20
     * @version: 1.0
     */
    public static class Add extends Function.UnInterruptedFunction {


        public Add(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            File base = DictionaryEnvironment.getInstance().getDicData().toFile();
            String name = args.get(0).toString();
            DictionaryFile file = new DictionaryFile(new File(base.getAbsolutePath() + "/" + name));
            try {
                file.parseDICCodeFile();
            } catch (IOException e) {
                throw new DictionaryOnRunningException("DIC解析失败:",e);
            }
            String alias = file.getName();

            if (args.size() >= 2) {
                alias = args.get(1).toString();
            }
            runtime.getFile().getSubFile()
                    .put(alias, file);
        }
    }


    public ModuleUtil(int line, String code) {
        super(line, code);
    }
}
