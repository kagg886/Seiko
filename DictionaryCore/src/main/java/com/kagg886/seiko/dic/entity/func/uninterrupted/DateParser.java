package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: DateParser
 * @author: kagg886
 * @description: 日期转换工具
 * @date: 2023/3/19 17:59
 * @version: 1.0
 */
public abstract class DateParser extends Function.UnInterruptedFunction {

    public DateParser(int line, String code) {
        super(line, code);
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: DateParser
     * @author: kagg886
     * @description: $转时间戳 存入变量 时间格式字符串 时间字符串$
     * @date: 2023/3/19 18:11
     * @version: 1.0
     */
    public static class DateToStamp extends Function.UnInterruptedFunction {

        public DateToStamp(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(args.get(1).toString(),Locale.CHINA);
            Date date;
            try {
                date = simpleDateFormat.parse(args.get(2).toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            runtime.getRuntimeObject().put(args.get(0).toString(),date.getTime());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: DateParser
     * @author: kagg886
     * @description: $转时间 存入变量 格式化字符串 毫秒时间戳$
     * @date: 2023/3/19 18:00
     * @version: 1.0
     */
    public static class StampToDate extends Function.UnInterruptedFunction {

        public StampToDate(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            long stamp = Long.parseLong(args.get(2).toString());
            String date = args.get(1).toString();
            runtime.getRuntimeObject().put(args.get(0).toString(),new SimpleDateFormat(date, Locale.CHINA).format(stamp));
        }
    }

}
