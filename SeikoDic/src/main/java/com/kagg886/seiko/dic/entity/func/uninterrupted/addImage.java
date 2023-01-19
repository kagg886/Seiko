package com.kagg886.seiko.dic.entity.func.uninterrupted;

import androidx.annotation.Keep;
import com.kagg886.seiko.dic.entity.impl.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.util.NetUtil;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.Call;
import okhttp3.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: Image
 * @author: kagg886
 * @description: TODO
 * @date: 2023/1/18 12:25
 * @version: 1.0
 */
@Keep
public class addImage extends Function.UnInterruptedFunction {

    public addImage(int line, String code) {
        super(line, code);
    }

    @Override
    public void run(AbsRuntime runtime) {
        Request req = new Request.Builder()
                .url(args.get(0))
                .get()
                .build();
        Call call = NetUtil.okHttpClient.newCall(req);
        try (InputStream s = Objects.requireNonNull(call.execute().body()).byteStream()) {
            runtime.getMessageCache().append(ExternalResource.uploadAsImage(s, runtime.getContact()));
        } catch (IOException e) {
            throw new DictionaryOnRunningException("上传图片失败!:" + args.get(0) + "在" + runtime.getFile().getFile().getAbsolutePath() + ":" + getLine(), e);
        }
    }
}
