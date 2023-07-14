package com.tencent.mobileqq.channel

abstract class ChannelProxy {
    abstract fun sendMessage(cmd: String, buffer: ByteArray, id: Long)
}