package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.session.impl.FunctionRuntime;
import com.kagg886.seiko.dic.session.impl.LifeCycleRuntime;

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
     * @description: $子文件调用 伪代码文件名 伪代码方法$
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
            runtime1.invoke(method);

        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.impl
     * @className: ModuleUtil
     * @author: kagg886
     * @description: $卸载 伪代码文件名$
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
     * @description: $装载 伪代码文件名$
     * @date: 2023/4/23 21:20
     * @version: 1.0
     */
    public static class Add extends Function.UnInterruptedFunction {


        public Add(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Path base = runtime.getFile().getFile().getParentFile().toPath();
            String name = args.get(0).toString();
            DictionaryFile file = new DictionaryFile(base.resolve(runtime.getFile().getName().split("\\.")[0] + "_sub").resolve(name).toFile());
            try {
                file.parseDICCodeFile();
            } catch (IOException e) {
                throw new DictionaryOnRunningException(e);
            }
            file.notifyLifeCycle(LifeCycleRuntime.LifeCycle.INIT);
            runtime.getFile().getSubFile()
                    .put(file.getName(), file);
        }
    }


    public ModuleUtil(int line, String code) {
        super(line, code);
    }
}
