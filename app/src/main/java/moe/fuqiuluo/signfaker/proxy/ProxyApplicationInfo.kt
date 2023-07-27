package moe.fuqiuluo.signfaker.proxy

import android.content.pm.ApplicationInfo
import moe.fuqiuluo.signfaker.logger.TextLogger.log

class ProxyApplicationInfo(
    private val myApplicationInfo: ApplicationInfo
): ApplicationInfo() {
    init {
        targetSdkVersion = 28
        log("ProxyApplicationInfo.targetSdkVersion = $targetSdkVersion")
        nativeLibraryDir = myApplicationInfo.nativeLibraryDir
        log("ProxyApplicationInfo.nativeLibraryDir = ${myApplicationInfo.nativeLibraryDir}")
    }
}