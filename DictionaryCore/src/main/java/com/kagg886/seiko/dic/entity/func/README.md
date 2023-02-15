# 扩展流程

1. 在`interrupted`或`uninterrupted`包里新建类。
2. 使类`extends Function的两个抽象子类`。即`InterruptedFunction`和`UnInterruptedFunction`
3. 在`Function`的全局数组中注册这个方法。格式为：
   `{"词库的方法头", "代表类的类名"}`
4. 打包，运行。

# 示例

```java
package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: Image
 * @author: kagg886
 * @description: $测试 A B C$
 * @date: 2023/2/15 20:27
 * @version: 1.0
 */
public class DemoFunction extends Function.UnInterruptedFunction {

    public addImage(int line, String code) {
        super(line, code);
    }

    @Override
    public void run(AbsRuntime<?> runtime, List<Object> args) {
        /*
        	获取词库变量表：runtime.getRuntimeObject()
        	args为词库传输的变量列表。无参数则列表为空
        */
    }
}

```

