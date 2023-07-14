package moe.fuqiuluo.signfaker.proxy

import android.content.pm.ApplicationInfo
import android.util.Log

class ProxyApplicationInfo(
    val myApplicationInfo: ApplicationInfo
) : ApplicationInfo() {

    fun log(s: String) {
        Log.d("ProxyApplicationInfo", s)
    }

    init {
        targetSdkVersion = 28
        log("ProxyApplicationInfo.targetSdkVersion = $targetSdkVersion")
        nativeLibraryDir = myApplicationInfo.nativeLibraryDir
        log("ProxyApplicationInfo.nativeLibraryDir = ${myApplicationInfo.nativeLibraryDir}")
    }
}