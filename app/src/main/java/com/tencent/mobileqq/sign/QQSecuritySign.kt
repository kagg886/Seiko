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

    //java/lang/String;
    // Ljava/lang/String;
    // Ljava/lang/String;
    // Ljava/lang/String;
    // Ljava/lang/String;
    // Ljava/lang/String;
    // Lcom/tencent/mobileqq/fe/EventCallback;)V
    external fun notifyCamera(
        str: String,
        str1: String,
        str2: String,
        str3: String,
        str4: String,
        str5: String,
        call: EventCallback
    )

    external fun dispatchEventPB(
        str: String,
        str2: String,
        bArr: ByteArray,
        eventCallback: EventCallback
    )

    external fun notify(str: String, str2: String, str3: String, eventCallback: EventCallback)

    external fun requestToken()

    //Failed to register native method com.tencent.mobileqq.sign.QQSecuritySign.uiNotify(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/tencent/mobileqq/fe/EventCallback;)V
    external fun uiNotify(str: String, str2: String, str3: String, call: EventCallback)


    class SignResult {
        var extra: ByteArray = EMPTY_BYTE_ARRAY
        var sign: ByteArray = EMPTY_BYTE_ARRAY
        var token: ByteArray = EMPTY_BYTE_ARRAY
    }
}