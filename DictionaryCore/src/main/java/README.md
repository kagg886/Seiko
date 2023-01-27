# 接入流程

## 1. 完善DictionaryEnvironment的参数

```text
DictionaryEnvironment.getInstance().setDicRoot(DIC存储的路径);
DictionaryEnvironment.getInstance().setErrorListener(new ErrorListener() {
    @Override
    public void onError(File p, Throwable e) {
        处理词库加载失败时的代码
    }
});
DictionaryEnvironment.getInstance().setDicConfigPoint(词库设置存储处，必须是JSON文件);
DictionaryEnvironment.getInstance().setDicData(词库运行时产生的文件);
```

## 2. 构造Runtime

继承`com.kagg886.seiko.dic.session.AbsRuntime`。

使用泛型来指定此次Runtime所匹配的事件类型

详情参见文件夹`com/kagg886/seiko/dic/session/impl`

> 未来将会进一步修改，让Runtime不仅仅支持Mirai的事件和消息结构

## 3. invoke它

使用`AbsRuntime.invoke(String message)`来进行解析。

一次使用后的AbsRuntime应该被丢弃销毁。
