package hamusuta

import net.mamoe.mirai.internal.utils.*
import net.mamoe.mirai.utils.BotConfiguration

object ProtocolUtils {
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
    fun injectProtocol() {
        MiraiProtocolInternal.protocols[BotConfiguration.MiraiProtocol.ANDROID_PHONE] = MiraiProtocolInternal(
            "com.tencent.mobileqq",
            537164840,
            "8.9.63",
            "8.9.63.11390",
            "6.0.0.2546",
            150470524,
            0x10400,
            34869472,
            "A6 B7 45 BF 24 A2 C2 77 52 77 16 F6 F3 6E B6 8D",
            1685069178L,
            20,
            "0S200MNJT807V3GE",
            false
        )
    }
}