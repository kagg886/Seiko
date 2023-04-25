package com.kagg886.seiko.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.util
 * @className: DisplayDateProxy
 * @author: kagg886
 * @description: 控制LocalDateTime格式化的东西
 * @date: 2023/4/25 19:20
 * @version: 1.0
 */
public class DisplayDateProxy {
    private final LocalDateTime time;

    private String formatStr;

    public DisplayDateProxy(LocalDateTime time) {
        this.time = time;
    }

    public long getTimeStamp() {
        return time.toInstant(ZoneId.systemDefault().getRules().getOffset(time)).toEpochMilli();
    }

    @Override
    public String toString() {
        if (formatStr == null) {
            return time.toString();
        }
        return new SimpleDateFormat(formatStr, Locale.CHINA).format(
                time.toInstant(ZoneId.systemDefault().getRules().getOffset(time)).toEpochMilli()
        );
    }

    public String getFormatStr() {
        return formatStr;
    }

    public void setFormatStr(String formatStr) {
        this.formatStr = formatStr;
    }
}
