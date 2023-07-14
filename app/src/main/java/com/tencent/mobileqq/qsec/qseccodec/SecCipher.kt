package com.tencent.mobileqq.qsec.qseccodec

object SecCipher {
    private const val SEC_INFO_TYPE_DECODE = 1
    private const val SEC_INFO_TYPE_ENCODE = 2
    private const val sVersion = "0.0.3"

    class SecInfo {
        var err = 0
        var result: Any? = null
        var ver = 0
    }

    private external fun codec(obj: Any, i2: Int): Any?

    fun decrypt(str: String): SecInfo? {
        return try {
            codec(str, SEC_INFO_TYPE_DECODE) as SecInfo?
        } catch (th: Throwable) {
            th.printStackTrace()
            null
        }
    }

    fun encrypt(str: String): SecInfo? {
        return try {
            codec(str, SEC_INFO_TYPE_ENCODE) as SecInfo?
        } catch (th: Throwable) {
            th.printStackTrace()
            null
        }
    }

    fun getVersion(): String {
        return sVersion
    }
}