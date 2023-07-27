package moe.fuqiuluo.signfaker.logger

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Message
import java.util.Date

object TextLogger {
    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("HH:mm:ss")
    private const val WHAT_INFO = 0

    fun log(str: String) {
        println(str)
    }

    fun input(str: String) {
        println(str)
    }
}