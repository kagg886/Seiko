package com.kagg886.seiko.bot.sign

import android.app.Activity
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.tencent.beacon.event.UserAction
import com.tencent.mobileqq.channel.ChannelManager
import com.tencent.mobileqq.channel.ChannelProxy
import com.tencent.mobileqq.dt.app.Dtc
import com.tencent.mobileqq.fe.FEKit
import com.tencent.mobileqq.qsec.qsecurity.QSecConfig
import moe.fuqiuluo.signfaker.proxy.ProxyContext
import net.mamoe.mirai.internal.spi.EncryptService
import net.mamoe.mirai.utils.Services
import net.mamoe.mirai.utils.toUHexString
import java.lang.ref.WeakReference

object QSignHelper {

    fun int32ToBuf(i: Int): ByteArray {
        val out = ByteArray(4)
        out[3] = i.toByte()
        out[2] = (i shr 8).toByte()
        out[1] = (i shr 16).toByte()
        out[0] = (i shr 24).toByte()
        return out
    }

    fun toUHex(a: ByteArray): String {
        return a.toUHexString("")
    }

    fun registerEncryptService() {
        Services.register(
            EncryptService.Factory::class.qualifiedName!!,
            SeikoEncryptServiceImpl::class.qualifiedName!!,
            ::SeikoEncryptServiceImpl
        )
    }

    fun initFEKit(context: Activity) {
        val ctx = ProxyContext(context)
        Dtc.ctx = WeakReference(ctx)
        UserAction.initUserAction(ctx, false)
        UserAction.setAppKey("0S200MNJT807V3E")
        UserAction.setAppVersion("8.9.68")

        var qimei = ""
        UserAction.getQimei {
            log("QIMEI FETCH 成功： $it")
            qimei = it
            QSecConfig.business_q36 = it
        }

        val qua = "V1_AND_SQ_8.9.68_4264_YYB_D"

        val cs = ctx.contentResolver

        val androidId = Settings.System.getString(cs, "android_id")

        Dtc.androidId = androidId

        FEKit.init(qua, qimei, androidId, ctx)
        ChannelManager.setChannelProxy(object : ChannelProxy() {
            override fun sendMessage(cmd: String, buffer: ByteArray, id: Long) {
                log("ChannelProxy.sendMessage($cmd, $buffer, $id)")
            }
        })
        ChannelManager.initReport(
            QSecConfig.business_qua,
            "7.0.300",
            Build.VERSION.RELEASE,
            Build.BRAND + Build.MODEL,
            QSecConfig.business_q36,
            QSecConfig.business_guid
        )
        ChannelManager.setCmdWhiteListChangeCallback { it ->
            it.forEach {
                log("Register for cmd: $it")
            }
        }
    }

    fun log(a: String) {
        Log.d("QSignHelper", a)
    }
}