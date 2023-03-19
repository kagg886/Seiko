package com.kagg886.seiko.dic.entity.func.interrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.util.TextUtils;
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
 * @description: $访问 结果存入变量 请求方式 网址 %头集合%(可以使用null占位) 参数集合/参数体$ 或 $访问 结果存入变量 %集合对象%$
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
        String url;
        HashMap<String,?> header;
        Object vars;
        boolean simpleResult;

        if (args.get(1) instanceof HashMap<?,?>) {
            //$访问 结果存入变量 请求方式 网址 %头集合%(可以使用null占位) 参数集合/参数体
            //传入的是集合对象，按照集合对象进行解析;
            HashMap<?,?> tmp = (HashMap<?, ?>) args.get(1);
            me = Connection.Method.valueOf(tmp.get("请求方式").toString());
            url = tmp.get("网址").toString();

            header = tmp.containsKey("头集合") ? (HashMap<String, ?>) tmp.get("头集合") : null;
            vars = tmp.get("参数");
            if (tmp.containsKey("简易信息")) {
                simpleResult = Boolean.parseBoolean(tmp.get("简易信息").toString());
            } else {
                simpleResult = true;
            }
        } else {
            me = Connection.Method.valueOf(args.get(1).toString());
            url = args.get(2).toString();
            //$访问 结果存入变量 请求方式 网址 %头集合%(可以使用null占位) 参数集合/参数体$
            header = args.size() >=  4 ? (args.get(3).equals("null") ? null : (HashMap<String, ?>) args.get(3)) : null;
            vars = args.size() >= 5 ? args.get(4) : null;
            simpleResult = true;
        }

        Connection conn = Jsoup.connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .method(me)
                .url(url);
        if (header != null) {
            for (Map.Entry<?,?> entry : header.entrySet()) {
                conn.header(entry.getKey().toString(),entry.getValue().toString());
            }
        }
        if (vars != null) {
            if (vars instanceof String) {
                conn.requestBody(vars.toString());
            } else if (vars instanceof HashMap<?,?>){
                HashMap<?,?> tmp = (HashMap<?, ?>) vars;
                for (Map.Entry<?,?> entry : tmp.entrySet()) {
                    conn.data(entry.getKey().toString(),entry.getValue().toString());
                }
            }
        }
        try {
            Connection.Response resp = conn.execute();
            if (simpleResult) {
                runtime.getRuntimeObject().put(putVar,resp.body());
                return;
            }
            HashMap<String,Object> details = new HashMap<>();
            details.put("响应码",resp.statusCode());
            details.put("响应头",resp.headers());
            details.put("响应内容", TextUtils.isEmpty(resp.body()) ? "" : resp.body());
            runtime.getRuntimeObject().put(putVar,details);
        } catch (IOException e) {
            throw new RuntimeException("访问网络出错!",e);
        }
//        if (args.size() >= 4) {
//            Object unknown = args.get(3);
//            if (unknown.toString().equals("null")) {
//                return;
//            }
//            if (unknown instanceof String) {
//                unknown = runtime.getRuntimeObject().get(unknown.toString());
//            }
//            HashMap<String, ?> headers_origin = (HashMap<String, ?>) unknown;
//
//            for (Map.Entry<String, ?> entry : headers_origin.entrySet()) {
//                conn.header(entry.getKey(), entry.getValue().toString());
//            }
//        }
//
//        if (args.size() == 5) {
//            Object paramUnknown = args.get(4);
//            if (paramUnknown instanceof String) {
//                conn.requestBody(paramUnknown.toString());
//            }
//            if (paramUnknown instanceof HashMap<?, ?>) {
//                HashMap<String, ?> headers_origin = (HashMap<String, ?>) paramUnknown;
//                for (Map.Entry<String, ?> entry : headers_origin.entrySet()) {
//                    conn.data(entry.getKey(), entry.getValue().toString());
//                }
//            }
//        }
//        try {
//            String ne = new String(conn.execute().bodyAsBytes());
//            runtime.getRuntimeObject().put(putVar, ne);
//        } catch (IOException e) {
//            throw new DictionaryOnRunningException("访问url出现错误!");
//        }
    }
}
