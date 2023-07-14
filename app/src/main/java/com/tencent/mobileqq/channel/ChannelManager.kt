package com.tencent.mobileqq.channel

import com.tencent.mobileqq.fe.CmdWhiteListChangeCallback

object ChannelManager {
    external fun initReport(
        str: String,
        channelVersion: String,
        str3: String,
        str4: String,
        str5: String,
        str6: String
    )

    external fun getCmdWhiteList(): ArrayList<String>

    external fun onNativeReceive(str: String, bArr: ByteArray, z: Boolean, j2: Long)

    external fun sendMessageTest()

    external fun setChannelProxy(channelProxy: ChannelProxy)

    external fun setCmdWhiteListChangeCallback(cmdWhiteListChangeCallback: CmdWhiteListChangeCallback)
}