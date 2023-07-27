package moe.fuqiuluo.signfaker.ext

data class SsoPacket(
    val cmd: String,
    val body: String,
    val callbackId: Long
)