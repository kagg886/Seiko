package com.tencent.mobileqq.dt.app

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Environment
import android.provider.Settings
import android.util.DisplayMetrics
import moe.fuqiuluo.signfaker.logger.TextLogger.log
import java.lang.ref.WeakReference

@SuppressLint("StaticFieldLeak")
object Dtc {
    lateinit var ctx: WeakReference<Context>
    private val cacheDB  = hashMapOf<String, String>()
    lateinit var androidId: String

    @JvmStatic
    fun mmKVValue(key: String): String {
        val ret =  when(key) {
            "o3_switch_Xwid" -> cacheDB.getOrDefault(key, "")

            else -> cacheDB.getOrDefault(key, "")
        }
        log("Dtc.mmkvValue(\"$key\") => \"$ret\"")
        return ret
    }

    @JvmStatic
    fun mmKVSaveValue(k: String, v: String) {
        log("Dtc.mmKVSaveValue(\"$k\", \"$v\")")
        cacheDB[k] = v
    }

    @SuppressLint("PrivateApi")
    @JvmStatic
    fun getPropSafe(str: String?): String? {
        return try {
            val cls = Class.forName("android.os.SystemProperties")
            cls.getMethod("get", String::class.java, String::class.java)
                .invoke(cls, str, "-1") as String
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    fun getAppVersionName(str: String?): String? {
        return try {
            "8.9.68"
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    fun getAppVersionCode(str: String?): String? {
        return try {
            "4264"
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    fun getAppInstallTime(str: String?): String? {
        return try {
            val context: Context = ctx.get()!!
            context.packageManager.getPackageInfo(
                context.packageName,
                0
            ).firstInstallTime.toString()
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    fun getDensity(str: String?): String? {
        return try {
            getContext().resources.displayMetrics.density.toString()
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    private fun getContext(): Context {
        return ctx.get()!!
    }

    @JvmStatic
    fun getFontDpi(str: String?): String? {
        return try {
            getContext().resources.displayMetrics.scaledDensity.toString()
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    fun getScreenSize(str: String?): String? {
        return try {
            val displayMetrics: DisplayMetrics = ctx.get()!!.resources.displayMetrics
            val j2 = getContext().resources.displayMetrics.heightPixels.toLong()
            val sb = StringBuilder()
            sb.append("[")
            sb.append(displayMetrics.widthPixels)
            sb.append(",")
            sb.append(j2)
            sb.append("]")
            sb.toString()
        } catch (th: Throwable) {
            "-1"
        }
    }


    @JvmStatic
    fun getStorage(str: String?): String? {
        return try {
            Environment.getDataDirectory().totalSpace.toString()
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    fun systemGetSafe(str: String): String? {
        return try {
            System.getProperty(str, "-1")
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    fun getIME(str: String?): String? {
        return try {
            Settings.System.getString(getContext().contentResolver, "default_input_method")
        } catch (th: Throwable) {
            "-1"
        }
    }

    @JvmStatic
    fun saveList(
        str: String?,
        str2: String?,
        str3: String?,
        str4: String?,
        str5: String?,
        str6: String?,
        str7: String?
    ) {}

    @JvmStatic
    fun getAndroidID(): String  = androidId

    @JvmStatic
    fun getBSSID(context: Context): String {
        return "-1"
    }
}