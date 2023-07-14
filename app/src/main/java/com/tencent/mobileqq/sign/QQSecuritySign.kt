package com.tencent.mobileqq.sign

import com.tencent.mobileqq.fe.EventCallback
import com.tencent.mobileqq.qsec.qsecurity.QSec


object QQSecuritySign {
    val EMPTY_BYTE_ARRAY = ByteArray(0)

    external fun initSafeMode(isSafe: Boolean)

    external fun getSign(
        qSec: QSec,
        str: String,
        str2: String,
        bArr: ByteArray,
        bArr2: ByteArray,
        str3: String
    ): SignResult?

    external fun dispatchEvent(str: String, str2: String, eventCallback: EventCallback)

    external fun dispatchEventPB(
        str: String,
        str2: String,
        bArr: ByteArray,
        eventCallback: EventCallback
    )

    external fun notify(str: String, str2: String, str3: String, eventCallback: EventCallback)

    external fun requestToken()

    class SignResult {
        var extra: ByteArray = EMPTY_BYTE_ARRAY
        var sign: ByteArray = EMPTY_BYTE_ARRAY
        var token: ByteArray = EMPTY_BYTE_ARRAY
    }
}