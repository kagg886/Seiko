package com.tencent.mobileqq.qsec.qsecurity

import android.content.Context

object QSecConfig {
    var CONFIG_KEY_BUF: ByteArray? = null
    var CONFIG_KEY_ID = 0
    var CONFIG_TIME_GAP = 5000
    const val CONST_CONFIG_TASK_ID = 1
    const val CONST_HEARTBEAT_TASK_ID = 0
    const val CONST_KEYEXCHANGE_TASK_ID = 2
    const val CONST_LUA_TASK_ID = 3
    const val CONST_REPORT_TASK_ID = 4
    const val DO_TYPE_DELE = 4
    const val DO_TYPE_INIT = 3
    const val DO_TYPE_START = 1
    const val DO_TYPE_STOP = 2

    var HEART_BEAT_SEQ_NUM = 0
    var business_guid = ""
    var business_o3did: String? = null
    var business_os = 1
    var business_q36 = ""
    var business_qua = ""
    var business_seed: String? = null
    var business_uin: String? = null
    var sContext: Context? = null
    var sign_strategy = 0

    fun setupBusinessInfo(
        context: Context?,
        uin: String?,
        guid: String,
        seed: String?,
        o3did: String?,
        q36: String,
        qua: String
    ) {
        sContext = context
        business_qua = qua
        business_uin = uin
        business_guid = guid
        business_seed = seed
        business_o3did = o3did
        business_q36 = q36
    }
}