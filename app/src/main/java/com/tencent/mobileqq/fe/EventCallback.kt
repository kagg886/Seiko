package com.tencent.mobileqq.fe

interface EventCallback {
    fun onResult(code: Int, pbData: ByteArray)
}