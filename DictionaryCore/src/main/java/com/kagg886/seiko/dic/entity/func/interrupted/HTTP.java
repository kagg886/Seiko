package com.kagg886.seiko.dic.entity.func.interrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.interrupted
 * @className: Delay
 * @author: kagg886
 * @description: $访问 结果存入变量 请求方式 网址 头集合(可以使用null占位) 参数集合/参数体$
 * @date: 2023/1/18 12:01
 * @version: 1.0
 */
public class HTTP extends Function.InterruptedFunction {

    public HTTP(int line, String code) {
        super(line, code);
    }

    @Override
    protected void run(AbsRuntime<?> runtime, List<Object> args) {
        String putVar = args.get(0).toString();
        Connection.Method me;
        try {
            me = Connection.Method.valueOf(args.get(1).toString());
        } catch (Exception e) {
            me = Connection.Method.GET;
        }
        String url = args.get(2).toString();


        Connection conn = Jsoup.connect(url).ignoreContentType(true).method(me);
        if (args.size() >= 4) {
            Object unknown = args.get(3);
            if (unknown.toString().equals("null")) {
                return;
            }
            HashMap<String, ?> headers_origin = (HashMap<String, ?>) unknown;

            for (Map.Entry<String, ?> entry : headers_origin.entrySet()) {
                conn.header(entry.getKey(), entry.getValue().toString());
            }
        }

        if (args.size() == 5) {
            Object paramUnknown = args.get(4);
            if (paramUnknown instanceof String) {
                conn.requestBody(paramUnknown.toString());
            }
            if (paramUnknown instanceof HashMap<?, ?>) {
                HashMap<String, ?> headers_origin = (HashMap<String, ?>) paramUnknown;
                for (Map.Entry<String, ?> entry : headers_origin.entrySet()) {
                    conn.data(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        try {
            String ne = new String(conn.execute().bodyAsBytes());
            runtime.getRuntimeObject().put(putVar, ne);
        } catch (IOException e) {
            throw new DictionaryOnRunningException("访问url出现错误!");
        }
    }
}
