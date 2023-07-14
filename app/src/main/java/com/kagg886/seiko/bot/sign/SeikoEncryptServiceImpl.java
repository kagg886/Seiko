package com.kagg886.seiko.bot.sign;

import android.annotation.SuppressLint;
import android.util.Log;
import com.tencent.mobileqq.fe.FEKit;
import com.tencent.mobileqq.qsec.qsecdandelionsdk.Dandelion;
import com.tencent.mobileqq.qsec.qsecurity.QSec;
import com.tencent.mobileqq.qsec.qsecurity.QSecConfig;
import com.tencent.mobileqq.sign.QQSecuritySign;
import kotlinx.coroutines.CoroutineScope;
import net.mamoe.mirai.internal.spi.EncryptService;
import net.mamoe.mirai.internal.spi.EncryptServiceContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * tlv544实现器，未来将支持更多类型
 *
 * @author kagg886
 * @date 2023/6/13 17:21
 **/
public class SeikoEncryptServiceImpl implements EncryptService.Factory, EncryptService {

    private Map<Long, Map<String, ?>> contextMap = new HashMap<>();

    private ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(10);

    //用户登录后才会被Patched
    public SeikoEncryptServiceImpl() {
    }

    /*
     * @param context: 上下文
     * @param tlvType: 类型
     * @param payload: 数据
     * @return byte
     * @author kagg886
     * @description 调用api获取tlv544数据
     * @see https://github.com/fuqiuluo/unidbg-fetch-qsign
     * @date 2023/07/01 13:28
     */
    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public byte[] encryptTlv(@NotNull EncryptServiceContext context, int tlvType, @NotNull byte[] payload) {
        if (tlvType != 0x544) return null;
        String command = (String) context.getExtraArgs().toMap().get("KEY_COMMAND_STR");
        byte[] res = Dandelion.INSTANCE.energy(command, payload);
        Log.d(getClass().getName(), "TLV544SignResult:" + QSignHelper.INSTANCE.toUHex(payload));
        return res;

/*        f-p-v前端
        val command = context.extraArgs[EncryptServiceContext.KEY_COMMAND_STR]
        val data = customEnergy(uin = context.id, salt = payload, data = command)
        return data.hexToBytes()

        private fun customEnergy(uin: Long, salt: ByteArray, data: String): String {
            val response = client.prepareGet("${server}/custom_energy")
                    .addQueryParam("uin", uin.toString())
                    .addQueryParam("salt", salt.toUHexString(""))
                    .addQueryParam("data", data)
                    .execute().get()
            val body = Json.decodeFromString(DataWrapper.serializer(), response.responseBody)
            check(body.code == 0) { body.message }

            logger.debug("Bot(${uin}) custom_energy ${data}, ${body.message}")

            return Json.decodeFromJsonElement(String.serializer(), body.data)
        }*/


        //unidbg后端
/*        val data = fetchGet("data")!!
        val salt = fetchGet("salt")!!.hex2ByteArray()

        log("API请求(/custom_energy): data = $data")

        val sign = Dandelion.energy(data, salt)

        call.respond(APIResult(if (sign == null) -1 else 0, "success", sign?.toHexString() ?: "null"))*/
    }

    @Override
    public void initialize(@NotNull EncryptServiceContext encryptServiceContext) {
        Log.d(getClass().getName(), "EncryptService Patch to Bot:" + encryptServiceContext.getId());
        contextMap.put(encryptServiceContext.getId(), encryptServiceContext.getExtraArgs().toMap());
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public synchronized SignResult qSecurityGetSign(@NotNull EncryptServiceContext encryptServiceContext, int seq, @NotNull String cmd, @NotNull byte[] bytes) {
        if (cmd.equals("StatSvc.register")) {
            QQSecuritySign.INSTANCE.requestToken();
            pool.schedule(QQSecuritySign.INSTANCE::requestToken, 30, TimeUnit.MINUTES);
        }

        long uin = encryptServiceContext.getId();
        String qua = QSecConfig.INSTANCE.getBusiness_qua();

        FEKit.INSTANCE.changeUin(uin);
        QQSecuritySign.SignResult result = QQSecuritySign.INSTANCE.getSign(QSec.INSTANCE, qua, cmd, bytes, QSignHelper.INSTANCE.int32ToBuf(seq), String.valueOf(uin));


        Log.d(getClass().getName(), "SSOSign:" + encryptServiceContext.getId() + "-->" + result);
        return new SignResult(result.getSign(), result.getToken(), result.getExtra());

/*        val response = client.preparePost("${server}/sign")
                .addFormParam("uin", uin.toString())
                .addFormParam("cmd", cmd)
                .addFormParam("seq", seq.toString())
                .addFormParam("buffer", buffer.toUHexString(""))
                .execute().get()
        val body = Json.decodeFromString(DataWrapper.serializer(), response.responseBody)
        check(body.code == 0) { body.message }

        logger.debug("Bot(${uin}) sign ${cmd}, ${body.message}")

        return Json.decodeFromJsonElement(SignResult.serializer(), body.data)*/


/*        val uin = fetchGet("uin")!!
                val qua = fetchGet("qua", CONFIG.protocol.qua)!!
                val cmd = fetchGet("cmd")!!
                val seq = fetchGet("seq")!!.toInt()
        val buffer = fetchGet("buffer")!!.hex2ByteArray()
        val qimei36 = fetchGet("qimei36", def = "")!!

                // I hope the androidId and guid can be null,but the fetchGet() can not work
                val androidId = call.parameters["android_id"] ?: ""
        val guid = call.parameters["guid"] ?: ""

        requestSign(cmd, uin, qua, seq, buffer, qimei36, androidId, guid)*/

/*        private suspend fun PipelineContext<Unit, ApplicationCall>.requestSign(cmd: String, uin: String, qua: String, seq: Int, buffer: ByteArray, qimei36: String = QSecConfig.business_q36) {
            FEKit.changeUin(uin.toLong())

            fun int32ToBuf(i: Int): ByteArray {
                val out = ByteArray(4)
                out[3] = i.toByte()
                out[2] = (i shr 8).toByte()
                out[1] = (i shr 16).toByte()
                out[0] = (i shr 24).toByte()
                return out
            }

            val sign = QQSecuritySign.getSign(QSec, qua, cmd, buffer, int32ToBuf(seq), uin)!!

                    call.respond(
                            APIResult(0, "success", Sign(
                                    sign.token.toHexString(),
                                    sign.extra.toHexString(),
                                    sign.sign.toHexString(), QSecConfig.business_o3did ?: ""
    )))
        }*/
    }

    @NotNull
    @Override
    public EncryptService createForBot(@NotNull EncryptServiceContext encryptServiceContext, @NotNull CoroutineScope coroutineScope) {
        return new SeikoEncryptServiceImpl();
    }
}
