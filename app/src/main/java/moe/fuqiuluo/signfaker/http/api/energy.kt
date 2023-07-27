package moe.fuqiuluo.signfaker.http.api

import com.tencent.mobileqq.qsec.qsecdandelionsdk.Dandelion
import com.tencent.mobileqq.sign.QQSecuritySign.EMPTY_BYTE_ARRAY
import moe.fuqiuluo.signfaker.ext.Crypt
import moe.fuqiuluo.signfaker.http.ext.hex2ByteArray
import moe.fuqiuluo.signfaker.http.ext.toHexString
import moe.fuqiuluo.utils.MD5
import java.nio.ByteBuffer
import java.security.InvalidParameterException

fun customEnergy(data: String, salt: ByteArray): String? {
    val sign = Dandelion.energy(data, salt)
    return sign?.toHexString()
}

fun energy(data: String, pMode: String?, uin: Long?, version: String?, pGuid: String?, pPhone: String?, pReceipt: String?, code: String?): String? {
    val mode = pMode ?: when(data) {
        "810_d", "810_a", "810_f", "810_9" -> "v2"
        "810_2", "810_25", "810_7", "810_24" -> "v1"
        "812_a" -> "v3"
        "812_5" -> "v4"
        else -> ""
    }
    val guid = pGuid?.hex2ByteArray()
    val phone = pPhone?.toByteArray()
    val receipt = pReceipt?.toByteArray()
    if(mode.isEmpty()) throw InvalidParameterException("need mode!")
    val salt = when (mode) {
        "v1" -> {
            if(uin == null || version == null || guid == null) throw NullPointerException("need parameter!")
            val salt = ByteBuffer.allocate(8 + 2 + guid.size + 2 + 10)
            salt.putLong(uin)
            salt.putShort(guid.size.toShort())
            salt.put(guid)
            salt.putShort(version.length.toShort())
            salt.put(version.toByteArray())
            salt.array()
        }
        "v2" -> {
            if(version == null || guid == null) throw NullPointerException("need parameter!")
            val sub = data.substring(4).toInt(16)
            val salt = ByteBuffer.allocate(4 + 2 + guid.size + 2 + 10 + 4 + 4)
            salt.putInt(0)
            salt.putShort(guid.size.toShort())
            salt.put(guid)
            salt.putShort(version.length.toShort())
            salt.put(version.toByteArray())
            salt.putInt(sub)
            salt.putInt(0)
            salt.array()
        }
        "v3" -> { // 812_a
            if(version == null || phone == null) throw NullPointerException("need parameter!")
            val salt = ByteBuffer.allocate(phone.size + 2 + 2 + version.length + 2)
            // 38 36 2D 31 37 33 36 30 32 32 39 31 37 32
            // 00 00
            // 00 06
            // 38 2E 39 2E 33 38
            // 00 00
            // result => 0C051B17347DF3B8EFDE849FC233C88DBEA23F5277099BB313A9CD000000004B744F7A00000000
            salt.put(phone)
            //println(String(phone))
            salt.putShort(0)
            salt.putShort(version.length.toShort())
            salt.put(version.toByteArray())
            salt.putShort(0)
            salt.array()
        }
        "v4" -> { // 812_5
            if(code == null || receipt == null) throw NullPointerException("need parameter!")
            val key = MD5.toMD5Byte(code)
            val encrypt = Crypt().encrypt(receipt, key)
            val salt = ByteBuffer.allocate(receipt.size + 2 + encrypt.size)
            salt.put(receipt)
            salt.putShort(encrypt.size.toShort())
            salt.put(encrypt)
            salt.array()
        }
        else -> {
            EMPTY_BYTE_ARRAY
        }
    }

    val sign = Dandelion.energy(data, salt)
    return sign?.toHexString()
}
