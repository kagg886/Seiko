@file:Suppress("UNCHECKED_CAST")
package moe.fuqiuluo.signfaker

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.tencent.beacon.event.UserAction
import com.tencent.mobileqq.channel.ChannelManager
import com.tencent.mobileqq.channel.ChannelProxy
import com.tencent.mobileqq.dt.app.Dtc
import com.tencent.mobileqq.fe.FEKit
import com.tencent.mobileqq.qsec.qsecurity.QSecConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import moe.fuqiuluo.signfaker.ext.GlobalData
import moe.fuqiuluo.signfaker.ext.SsoPacket
import moe.fuqiuluo.signfaker.http.ext.toHexString
import moe.fuqiuluo.signfaker.logger.TextLogger
import moe.fuqiuluo.signfaker.proxy.ProxyContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.lang.ref.WeakReference
import java.security.Security
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean

object Starter {
    val global = GlobalData()
    init {
        Security.addProvider(BouncyCastleProvider())
        global["PACKET"] = arrayListOf<SsoPacket>()
    }

    private val isInit = AtomicBoolean(false)

    fun start(context: Context, androidId: String, guid: String, qimei: String) {
        if (isInit.compareAndSet(false, true)) {
            initFEKit(context, androidId, guid, qimei)
        }
    }

    private fun initFEKit(context: Context, androidId: String, guid: String, qimei: String) {
        val ctx = ProxyContext(context)
        Dtc.ctx = WeakReference(ctx)
        UserAction.initUserAction(ctx, false)
        UserAction.setAppKey("0S200MNJT807V3GE")
        UserAction.setAppVersion("9.0.8")
        UserAction.getQimei {
            TextLogger.log("QIMEI FETCH 成功： $it")
//            qimei = it
            QSecConfig.business_q36 = qimei
        }
        val qua = "V1_AND_SQ_9.0.8_5540_YYB_D"
        Dtc.androidId = androidId
        FEKit.init(qua, qimei, androidId, ctx)
        ChannelManager.setChannelProxy(object: ChannelProxy() {
            override fun sendMessage(cmd: String, buffer: ByteArray, id: Long) {
                val hex = buffer.toHexString()
                if (id == -1L) return
                (global["PACKET"] as ArrayList<SsoPacket>).add(SsoPacket(cmd, hex, id))
                TextLogger.log("ChannelProxy.sendMessage($cmd, $buffer, $id)")
                return
            }
        })
        ChannelManager.initReport(qua, "7.0.300", Build.VERSION.RELEASE, Build.BRAND + Build.MODEL, qimei, guid)
        ChannelManager.setCmdWhiteListChangeCallback {
            it.forEach {
                TextLogger.log("Register for cmd: $it")
            }
        }
    }
}