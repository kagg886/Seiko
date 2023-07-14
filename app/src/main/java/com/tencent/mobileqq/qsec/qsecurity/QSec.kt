package com.tencent.mobileqq.qsec.qsecurity

import android.content.Context

object QSec {
    external fun doReport(str: String, str2: String, str3: String, str4: String): Int

    external fun doSomething(context: Context, i2: Int): Int

    fun updateO3DID(str: String) {
        QSecConfig.business_o3did = str
    }
}