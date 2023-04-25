package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.util.DisplayDateProxy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
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
     * @description: $转时间戳 存入变量 时间对象$ 或 $转时间戳 存入变量 时间格式字符串 时间字符串 $
     * @date: 2023/3/19 18:11
     * @version: 1.0
     */
    public static class DateToStamp extends Function.UnInterruptedFunction {

        public DateToStamp(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            if (args.get(1) instanceof DisplayDateProxy) {
                runtime.getRuntimeObject().put(args.get(0).toString(), ((DisplayDateProxy) args.get(1)).getTimeStamp());
                return;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(args.get(1).toString(), Locale.CHINA);
            Date date;
            try {
                date = simpleDateFormat.parse(args.get(2).toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            runtime.getRuntimeObject().put(args.get(0).toString(), date.getTime());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: DateParser
     * @author: kagg886
     * @description: $时间格式化 存入变量 毫秒时间戳$ 或 $时间格式化 存入变量 不能是数字的占位符 年 月 日 时 分 秒 毫秒$
     * @date: 2023/3/19 18:00
     * @version: 1.0
     */
    public static class StampToDateNew extends Function.UnInterruptedFunction {

        public StampToDateNew(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            try {
                long stamp = Long.parseLong(args.get(1).toString());
                runtime.getRuntimeObject().put(
                        args.get(0).toString(),
                        new DisplayDateProxy(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(stamp),
                                ZoneId.systemDefault()
                        ))
                );
                return;
            } catch (Exception ignored) {
            }

            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonth().getValue();
            int day = LocalDate.now().getDayOfMonth();
            int hour = LocalTime.now().getHour();
            int min = LocalTime.now().getMinute();
            int sec = LocalTime.now().getSecond();
            int nano = LocalTime.now().getNano();

            try {
                year = Integer.parseInt(args.get(2).toString());
            } catch (Exception ignored) {
            }

            try {
                month = Integer.parseInt(args.get(3).toString());
            } catch (NumberFormatException ignored) {
                month = 1;
            } catch (Exception ignored) {

            }

            try {
                day = Integer.parseInt(args.get(4).toString());
            } catch (NumberFormatException ignored) {
                day = 1;
            } catch (Exception ignored) {
            }

            try {
                hour = Integer.parseInt(args.get(5).toString());
            } catch (NumberFormatException ignored) {
                hour = 0;
            } catch (Exception ignored) {
            }

            try {
                min = Integer.parseInt(args.get(6).toString());
            } catch (NumberFormatException ignored) {
                min = 0;
            } catch (Exception ignored) {
            }

            try {
                sec = Integer.parseInt(args.get(7).toString());
            } catch (NumberFormatException ignored) {
                sec = 0;
            } catch (Exception ignored) {
            }

            try {
                nano = Integer.parseInt(args.get(8).toString()) * 1000000; //转纳秒
            } catch (NumberFormatException ignored) {
                nano = 0;
            } catch (Exception ignored) {
            }

            LocalDateTime time = LocalDateTime.of(year, month, day, hour, min, sec, nano);
            runtime.getRuntimeObject().put(
                    args.get(0).toString(), new DisplayDateProxy(time)
            );
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: DateParser
     * @author: kagg886
     * @description: $设置时间格式 时间对象 时间格式字符串$
     * @date: 2023/4/25 20:03
     * @version: 1.0
     */
    public static class StampSet extends Function.UnInterruptedFunction implements ArgumentLimiter {

        public StampSet(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            if (args.get(0) instanceof DisplayDateProxy) {
                DisplayDateProxy proxy = ((DisplayDateProxy) args.get(0));
                proxy.setFormatStr(args.get(1).toString());
                return;
            } else {
                throw new DictionaryOnRunningException(args.get(0).toString() + "不是时间!");
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
     * @className: DateParser
     * @author: kagg886
     * @description: $转时间 存入变量 格式化字符串 毫秒时间戳$ 或 $转时间 存入变量 时间对象 格式化字符串(可选)$
     * @date: 2023/3/19 18:00
     * @version: 1.0
     */
    public static class StampToDate extends Function.UnInterruptedFunction {

        public StampToDate(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            if (args.get(1) instanceof DisplayDateProxy) {
                DisplayDateProxy proxy = ((DisplayDateProxy) args.get(1));
                if (args.size() > 3) {
                    proxy.setFormatStr(args.get(2).toString());
                }
                runtime.getRuntimeObject().put(args.get(0).toString(), proxy.toString());
                return;
            }

            long stamp = Long.parseLong(args.get(2).toString());
            String date = args.get(1).toString();
            runtime.getRuntimeObject().put(args.get(0).toString(), new SimpleDateFormat(date, Locale.CHINA).format(stamp));
        }
    }

}
