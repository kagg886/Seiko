package hamusuta

import net.mamoe.mirai.utils.BotConfiguration

object ProtocolUtils {
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
    //{
    //    "apk_id": "com.tencent.mobileqq",
    //    "app_id": 537170996,
    //    "sub_app_id": 537170996,
    //    "app_key": "0S200MNJT807V3GE",
    //    "sort_version_name": "8.9.73.11945",
    //    "build_time": 1690371091,
    //    "apk_sign": "a6 b7 45 bf 24 a2 c2 77 52 77 16 f6 f3 6e b6 8d",
    //    "sdk_version": "6.0.0.2553",
    //    "sso_version": 20,
    //    "misc_bitmap": 150470524,
    //    "main_sig_map": 16724722,
    //    "sub_sig_map": 66560,
    //    "dump_time": 1691057433,
    //    "qua": "V1_AND_SQ_8.9.73_4416_YYB_D",
    //    "protocol_type": 1
    //}
    fun injectProtocol() {
        net.mamoe.mirai.internal.utils.MiraiProtocolInternal.protocols[BotConfiguration.MiraiProtocol.ANDROID_PHONE] = net.mamoe.mirai.internal.utils.MiraiProtocolInternal(
            "com.tencent.mobileqq",
            537170996,
            "8.9.73",
            "8.9.73.11945",
            "6.0.0.2553",
            150470524,
            66560,
            16724722,
            "A6 B7 45 BF 24 A2 C2 77 52 77 16 F6 F3 6E B6 8D",
            1690371091L,
            20,
            "0S200MNJT807V3GE",
            false,
            true
        )
    }
}