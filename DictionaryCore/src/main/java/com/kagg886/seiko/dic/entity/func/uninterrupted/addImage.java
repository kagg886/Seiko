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
 * @description: $图片 link$
 * @date: 2023/1/18 12:25
 * @version: 1.0
 */
public class addImage extends Function.UnInterruptedFunction {

    public addImage(int line, String code) {
        super(line, code);
    }

    @Override
    public void run(AbsRuntime<?> runtime, List<Object> args) {
        Connection conn = Jsoup.connect(args.get(0).toString()).ignoreContentType(true);
        try (InputStream s = conn.execute().bodyStream()) {
            runtime.getMessageCache().append(ExternalResource.uploadAsImage(s, runtime.getContact()));
        } catch (IOException e) {
            throw new DictionaryOnRunningException("上传图片失败!:" + args.get(0) + "在" + runtime.getFile().getFile().getAbsolutePath() + ":" + getLine(), e);
        }
    }
}
