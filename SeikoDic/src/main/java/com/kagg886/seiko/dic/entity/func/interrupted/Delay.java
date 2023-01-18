package com.kagg886.seiko.dic.entity.func.interrupted;

import androidx.annotation.Keep;
import com.kagg886.seiko.dic.entity.impl.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.interrupted
 * @className: Delay
 * @author: kagg886
 * @description: TODO
 * @date: 2023/1/18 12:01
 * @version: 1.0
 */
@Keep
public class Delay extends Function.InterruptedFunction {
    public Delay(int line, String code) {
        super(line, code);
    }

    @Override
    public void run(AbsRuntime runtime) {
        try {
            Thread.sleep(Integer.parseInt(args.get(0)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
