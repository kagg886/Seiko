package com.kagg886.seiko.bot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
import net.mamoe.mirai.internal.spi.EncryptService
import net.mamoe.mirai.internal.spi.EncryptServiceContext
import net.mamoe.mirai.utils.BotConfiguration
import kotlin.system.exitProcess

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
class SeikoEncryptServiceFactory: EncryptService.Factory {
    companion object {
        private val created: MutableSet<Long> = java.util.concurrent.ConcurrentHashMap.newKeySet()
    }
    override fun createForBot(
        context: EncryptServiceContext,
        serviceSubScope: CoroutineScope
    ): EncryptService {
        if (created.add(context.id).not()) {
            throw UnsupportedOperationException("repeated create EncryptService")
        }
        serviceSubScope.coroutineContext.job.invokeOnCompletion {
            created.remove(context.id)
        }
        return when (val protocol = context.extraArgs[EncryptServiceContext.KEY_BOT_PROTOCOL]) {
            BotConfiguration.MiraiProtocol.ANDROID_PHONE, BotConfiguration.MiraiProtocol.ANDROID_PAD -> {
                UnidbgFetchQsign(
                    coroutineContext = serviceSubScope.coroutineContext
                )
            }
            BotConfiguration.MiraiProtocol.ANDROID_WATCH -> throw UnsupportedOperationException(protocol.name)
            BotConfiguration.MiraiProtocol.IPAD, BotConfiguration.MiraiProtocol.MACOS -> {
                throw UnsupportedOperationException("不支持的协议")
            }
            else -> throw UnsupportedOperationException("未知协议")
        }
    }
}