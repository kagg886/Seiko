package com.tencent.mobileqq.fe

import android.util.Log
import com.tencent.beacon.event.UserAction
import com.tencent.mobileqq.dt.Dtn
import com.tencent.mobileqq.fe.utils.DeepSleepDetector
import com.tencent.mobileqq.qsec.qsecurity.QSec
import com.tencent.mobileqq.qsec.qsecurity.QSecConfig
import com.tencent.mobileqq.sign.QQSecuritySign
import moe.fuqiuluo.signfaker.proxy.ProxyContext
import java.io.File
import java.security.MessageDigest

object FEKit {

    fun log(s: String) {
        Log.d("FEKit", s)
    }

    fun init(qua: String, qimei: String, androidId: String, proxyContext: ProxyContext) {
        kotlin.runCatching {
            log("尝试载入FEKIT二进制库...")
            System.loadLibrary("fekit")
            log("载入FEKIT二进制库成功...")

            QSecConfig.setupBusinessInfo(proxyContext, "0", toMD5(androidId + "02:00:00:00:00:00"), "", "", qimei, qua)

            DeepSleepDetector.startCheck()

            QQSecuritySign.initSafeMode(false)
            log("设置安全模式 = false")

            val file = File(proxyContext.getFilesDirV2(), "5463306EE50FE3AA")
            if (!file.exists()) {
                log("目录`5463306EE50FE3AA`不存在，创建成功！")
                file.mkdirs()
            }
            Dtn.initContext(proxyContext, file.absolutePath)
            log("初始化Dtn成功")
            Dtn.initLog(object : IFEKitLog() {
                override fun d(str: String, i2: Int, str2: String) {
                    log("FEKitLogDebug $str: $str2")
                }

                override fun e(str: String?, i2: Int, str2: String?) {
                    log("FEKitLogErr $str: $str2")
                }

                override fun i(str: String?, i2: Int, str2: String?) {
                    log("FEKitLogInfo $str: $str2")
                }

                override fun v(str: String?, i2: Int, str2: String?) {
                    log("FEKitLogV $str: $str2")
                }

                override fun w(str: String?, i2: Int, str2: String?) {
                    log("FEKitLogWarn $str: $str2")
                }
            })
            log("尝试初始化Xwid for empty uin")
            Dtn.initUin("0")
            log("初始化init_uin成功")

            QSec.doSomething(proxyContext, 1)

            log("FEKIT初始化结束")
        }.onFailure {
            log("错误：${it.stackTraceToString()}")
        }
    }

    fun changeUin(uin: Long) {
        UserAction.setQQ(uin.toString())
        QSecConfig.business_uin = uin.toString()
        Dtn.initUin(uin.toString())
        log("改变Uin = $uin")
    }

    private fun toMD5(input: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(input.toByteArray())
        val stringBuilder = StringBuilder()

        //转成16进制
        result.forEach {
            val value = it
            val hex = value.toInt() and (0xFF)
            val hexStr = Integer.toHexString(hex)
            println(hexStr)
            if (hexStr.length == 1) {
                stringBuilder.append(0).append(hexStr)
            } else {
                stringBuilder.append(hexStr)
            }
        }

        return stringBuilder.toString()
    }
}