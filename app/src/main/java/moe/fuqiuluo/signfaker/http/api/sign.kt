package moe.fuqiuluo.signfaker.http.api

import com.tencent.mobileqq.fe.FEKit
import com.tencent.mobileqq.qsec.qsecurity.QSec
import com.tencent.mobileqq.qsec.qsecurity.QSecConfig
import com.tencent.mobileqq.sign.QQSecuritySign
import moe.fuqiuluo.signfaker.http.ext.hex2ByteArray
import moe.fuqiuluo.signfaker.http.ext.toHexString

fun sign(uin: String, cmd: String, seq: Int, buffer: ByteArray, qua: String=QSecConfig.business_qua, qimei36: String = QSecConfig.business_q36): Sign {
    return requestSign(cmd, uin, qua, seq, buffer, qimei36)
}


data class Sign(
    val token: String,
    val extra: String,
    val sign: String,
    val o3did: String
)

private fun requestSign(cmd: String, uin: String, qua: String, seq: Int, buffer: ByteArray, qimei36: String = QSecConfig.business_q36): Sign {
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

    return Sign(
        sign.token.toHexString(),
        sign.extra.toHexString(),
        sign.sign.toHexString(), QSecConfig.business_o3did ?: ""
    )
}