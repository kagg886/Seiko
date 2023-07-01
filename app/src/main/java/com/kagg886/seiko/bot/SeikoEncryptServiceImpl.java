package com.kagg886.seiko.bot;

import android.annotation.SuppressLint;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.util.TextUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.internal.spi.EncryptService;
import net.mamoe.mirai.internal.spi.EncryptServiceContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * tlv544实现器，未来将支持更多类型
 *
 * @author kagg886
 * @date 2023/6/13 17:21
 **/
public class SeikoEncryptServiceImpl implements EncryptService {

    private Map<Long, Map<String, ?>> contextMap = new HashMap<>();

    public SeikoEncryptServiceImpl() {
        Log.d(getClass().getName(), "EncryptService Patched!");
    }

    /*
     * @param context: 上下文
     * @param tlvType: 类型
     * @param payload: 数据
     * @return byte
     * @author kagg886
     * @description 调用在线api获取tlv544数据
     * @see https://github.com/fuqiuluo/unidbg-fetch-qsign
     * @date 2023/07/01 13:28
     */
    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public byte[] encryptTlv(@NotNull EncryptServiceContext context, int tlvType, @NotNull byte[] payload) {
//        //本地签名
//        if (tlvType != 0x544) return null;
//        Bot.getInstance(context.getId()).getLogger().debug(String.format("encryptTLV,type=0x%s:", Integer.toHexString(tlvType)));
//
//        byte[] bytes;
//        if (payload[payload.length - 1] == 0) {
//            bytes = ObjectUtils.copyInto(payload, new byte[payload.length], 4, 4, payload.length);
//        } else {
//            bytes = payload;
//        }
//        return Tlv544Sign.signBytes(bytes);


        if (tlvType != 0x544) return null;
        Map<String, Object> map = context.getExtraArgs().toMap();
        String cmd = (String) map.get("KEY_COMMAND_STR");
        Bot.getInstance(context.getId()).getLogger().debug(String.format("encryptTLV544:cmd->%s", cmd));

        try {
            //# http://127.0.0.1:8080/energy?&version=[VERSION]&uin=[UIN]&guid=[GUID]&data=[DATA]
            //[VERSION]：注意！这里的VERSION指的不是QQ的版本号，而是SDK Version，可以在QQ安装包中找到此信息。实例值：“6.0.0.2534”
            //[UIN]：Bot的QQ号。实例值：“11451419198”
            //[GUID]：密文，将byte数组转换为HEX发送，一般不会很长，不会超过GET上限。实例值：“0C099F0C099F0C099F”
            //[DATA]：实际上是"mode"，QQ在运行时会随机挑选一个作为加密题目，要根据题目算出密文结果才能通过认证。实例值：“810_d”
            JSONObject a = JSON.parseObject(Jsoup.connect("https://qsign.yslsy.top/custom_energy").ignoreContentType(true)
                    .data("salt", TextUtils.toHEXString(payload))
                    .data("data", cmd).execute().body());
            if (a.getInteger("code").equals(0)) {
                return ktutil.INSTANCE.hexToBytes(a.getString("data"));
            }
            throw new RuntimeException(a.getString("msg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(@NotNull EncryptServiceContext encryptServiceContext) {
        contextMap.put(encryptServiceContext.getId(), encryptServiceContext.getExtraArgs().toMap());
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public SignResult qSecurityGetSign(@NotNull EncryptServiceContext encryptServiceContext, int sequenceId, @NotNull String cmdName, @NotNull byte[] bytes) {
        //# http://127.0.0.1:8080/sign?uin=[UIN]&qua=[QUA]&cmd=[CMD]&seq=[SEQ]&buffer=[BUFFER]
        //[UIN]：Bot的QQ号。实例值：“11451419198”
        //[QUA]：是手机QQ运行时，APP的某些信息，一般会在启动时得到。实例值：“V1_AND_SQ_8.9.63_4188_HDBM_T”
        //[CMD]：指令类型，在做什么的时候需要的sign就什么时候用，需要注意的是不仅登录的时候需要sign，发信息也需要带sign，所以CMD才有很多种。实例值：“wtlogin.login”
        //[SEQ]：意义不明的签名数字戳，看起来像时间戳。实例值：“1848698645”
        //[BUFFER]：密文，将byte数组转换为HEX发送。实例值：“0C099F0C099F0C099F”
        //
        //因为有些时候密文会过长，导致超出get的长度上限，因此sign支持POST
        //content-type为application/x-www-form-urlencoded
        //正文和GET写法格式一样："uin=" + qq + "&qua=" + qua + "&cmd=" + cmd + "&seq=" + seq + "&buffer=" + DataUtils.byteArrayToHex(buffer)

        Map<String, ?> ctx = contextMap.get(encryptServiceContext.getId());
        Bot.getInstance(encryptServiceContext.getId()).getLogger().debug(String.format("runSSOSign->:%s(%d)", cmdName, sequenceId));
        Connection c = Jsoup.connect("https://qsign.yslsy.top/sign").ignoreContentType(true)
                .header("Content-type", "application/x-www-form-urlencoded")
                .data("uin", String.valueOf(encryptServiceContext.getId()))
                .data("qua", (String) ctx.get("KEY_APP_QUA"))
                .data("cmd", cmdName)
                .data("seq", String.valueOf(sequenceId))
                .data("buffer", ktutil.INSTANCE.getUHex(bytes));
        JSONObject json;
        try {
            json = JSON.parseObject(c.execute().body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (json.getInteger("code").equals(0)) {
            json = json.getJSONObject("data");
            return new EncryptService.SignResult(
                    ktutil.INSTANCE.hexToBytes(json.getString("sign")),
                    ktutil.INSTANCE.hexToBytes(json.getString("token")),
                    ktutil.INSTANCE.hexToBytes(json.getString("extra"))
            );
        }
        throw new RuntimeException(json.getString("msg"));
    }
}
