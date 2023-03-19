package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: MessageControl
 * @author: kagg886
 * @description: 消息有关
 * @date: 2023/3/19 22:15
 * @version: 1.0
 */
public abstract class MessageControl extends Function.UnInterruptedFunction {

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: addAt
     * @author: kagg886
     * @description: $艾特 qq$(当QQ为0时为全体艾特)
     * @date: 2023/1/18 12:25
     * @version: 1.0
     */
    public class addAt extends Function.UnInterruptedFunction {

        public addAt(int line, String code) {
            super(line, code);
        }

        @Override
        public void run(AbsRuntime<?> runtime, List<Object> args) {
            long at = Long.parseLong(args.get(0).toString());
            if (at != 0) {
                runtime.getMessageCache().append(new At(at));
            } else {
                runtime.getMessageCache().append(AtAll.INSTANCE);
            }
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: addImage
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


    public MessageControl(int line, String code) {
        super(line, code);
    }
}
