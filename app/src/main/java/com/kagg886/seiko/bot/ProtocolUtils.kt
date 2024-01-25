package hamusuta

import net.mamoe.mirai.utils.BotConfiguration

object ProtocolUtils {
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

//    {
//        "apk_id": "com.tencent.mobileqq",
//        "app_id": 537176863,
//        "sub_app_id": 537176863,
//        "app_key": "0S200MNJT807V3GE",
//        "sort_version_name": "8.9.80.12440",
//        "build_time": 1691565978,
//        "apk_sign": "a6b745bf24a2c277527716f6f36eb68d",
//        "sdk_version": "6.0.0.2554",
//        "sso_version": 20,
//        "misc_bitmap": 150470524,
//        "main_sig_map": 16724722,
//        "sub_sig_map": 66560,
//        "dump_time": 1692110632,
//        "qua": "V1_AND_SQ_8.9.80_4614_YYB_D",
//        "protocol_type": 1
//    }

    fun injectProtocol() {
        net.mamoe.mirai.internal.utils.MiraiProtocolInternal.protocols[BotConfiguration.MiraiProtocol.ANDROID_PHONE] = net.mamoe.mirai.internal.utils.MiraiProtocolInternal(
            "com.tencent.mobileqq",
            537200219,
            "8.9.80",
            "9.0.8.14755",
            "6.0.0.2558",
            150470524,
            66560,
            16724722,
            "a6b745bf24a2c277527716f6f36eb68d",
            1702888273L,
            21,
            "0S200MNJT807V3GE",
            false,
            true
        )
    }
}