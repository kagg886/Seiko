package com.tencent.mobileqq.qsec.qsecprotocol

import android.content.Context

object ByteData {
    private external fun getByte(context: Context, obj: Any): ByteArray?
}