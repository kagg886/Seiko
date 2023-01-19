package com.kagg886.seiko.dic.entity.func.interrupted;

import androidx.annotation.Keep;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.interrupted
 * @className: Delay
 * @author: kagg886
 * @description: $延时 毫秒数$
 * @date: 2023/1/18 12:01
 * @version: 1.0
 */
@Keep
public class Delay extends Function.InterruptedFunction {
    public Delay(int line, String code) {
        super(line, code);
    }

    @Override
    public void run(AbsRuntime runtime, Object[] args) {
        try {
            Thread.sleep(Integer.parseInt(args[0].toString()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
