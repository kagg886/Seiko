@file:Suppress("UNCHECKED_CAST")
package com.kagg886.seiko.bot

import com.kagg886.seiko.SeikoApplication
import com.tencent.mobileqq.channel.ChannelManager
import com.tencent.mobileqq.sign.QQSecuritySign
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonNames
import moe.fuqiuluo.signfaker.Starter
import moe.fuqiuluo.signfaker.ext.SsoPacket
import moe.fuqiuluo.signfaker.http.api.Sign
import moe.fuqiuluo.signfaker.http.api.customEnergy
import moe.fuqiuluo.signfaker.http.api.sign
import moe.fuqiuluo.signfaker.http.ext.hex2ByteArray
import net.mamoe.mirai.internal.spi.EncryptService
import net.mamoe.mirai.internal.spi.EncryptServiceContext
import net.mamoe.mirai.utils.MiraiInternalApi
import net.mamoe.mirai.utils.MiraiLogger
import kotlin.coroutines.CoroutineContext

class UnidbgFetchQsign(coroutineContext: CoroutineContext): EncryptService, CoroutineScope {
    private var channel0: EncryptService.ChannelProxy? = null
    private val token = java.util.concurrent.atomic.AtomicLong(0)
    private val channel: EncryptService.ChannelProxy get() = channel0 ?: throw IllegalStateException("need initialize")

    override val coroutineContext: CoroutineContext =
        coroutineContext + SupervisorJob(coroutineContext[Job]) + CoroutineExceptionHandler { context, exception ->
            when (exception) {
                is CancellationException -> {
                    // ...
                }
                else -> {
                    println("error: " + exception.message)
                    exception.printStackTrace()
                }
            }
        }

    override fun encryptTlv(context: EncryptServiceContext, tlvType: Int, payload: ByteArray): ByteArray? {
        if (tlvType != 0x544) return null
        val command = context.extraArgs[EncryptServiceContext.KEY_COMMAND_STR]
        val data = customEnergy0(salt = payload, data = command)
        println("encryptTlv: $data")
        return data.hex2ByteArray()
    }

    private fun customEnergy0(salt: ByteArray, data: String): String {
        println("customEnergy0")
        return customEnergy(data, salt)!!
    }

    @OptIn(MiraiInternalApi::class)
    override fun initialize(context: EncryptServiceContext) {
        println("initialize")
        val device = context.extraArgs[EncryptServiceContext.KEY_DEVICE_INFO]
        val qimei36 = context.extraArgs[EncryptServiceContext.KEY_QIMEI36]
        val uin = context.id
        val androidId = device.androidId.decodeToString()
        val guid = device.guid.decodeToString()
        channel0 = context.extraArgs[EncryptServiceContext.KEY_CHANNEL_PROXY]
        if(token.get() == 0L) {
            Starter.start(SeikoApplication.getSeikoApplicationContext(), androidId, guid, qimei36)
            coroutineContext.job.invokeOnCompletion {
                token.compareAndSet(uin, 0)
            }
        }
    }


    companion object {
        @JvmStatic

        internal val CMD_WHITE_LIST = """
            OidbSvcTrpcTcp.0x55f_0
            OidbSvcTrpcTcp.0x1100_1
            qidianservice.269
            OidbSvc.0x4ff_9_IMCore
            MsgProxy.SendMsg
            SQQzoneSvc.shuoshuo
            OidbSvc.0x758_1
            QChannelSvr.trpc.qchannel.commwriter.ComWriter.DoReply
            trpc.login.ecdh.EcdhService.SsoNTLoginPasswordLoginUnusualDevice
            wtlogin.device_lock
            OidbSvc.0x758_0
            wtlogin_device.tran_sim_emp
            OidbSvc.0x4ff_9
            trpc.springfestival.redpacket.LuckyBag.SsoSubmitGrade
            FeedCloudSvr.trpc.feedcloud.commwriter.ComWriter.DoReply
            trpc.o3.report.Report.SsoReport
            SQQzoneSvc.addReply
            OidbSvc.0x8a1_7
            QChannelSvr.trpc.qchannel.commwriter.ComWriter.DoComment
            OidbSvcTrpcTcp.0xf67_1
            friendlist.ModifyGroupInfoReq
            OidbSvcTrpcTcp.0xf65_1
            OidbSvcTrpcTcp.0xf65_10
            OidbSvcTrpcTcp.0xf65_10
            OidbSvcTrpcTcp.0xf67_5
            OidbSvc.0x56c_6
            OidbSvc.0x8ba
            SQQzoneSvc.like
            OidbSvcTrpcTcp.0xf88_1
            OidbSvc.0x8a1_0
            wtlogin.name2uin
            SQQzoneSvc.addComment
            wtlogin.login
            trpc.o3.ecdh_access.EcdhAccess.SsoSecureA2Access
            OidbSvcTrpcTcp.0x101e_2
            qidianservice.135
            FeedCloudSvr.trpc.feedcloud.commwriter.ComWriter.DoComment
            FeedCloudSvr.trpc.feedcloud.commwriter.ComWriter.DoBarrage
            OidbSvcTrpcTcp.0x101e_1
            OidbSvc.0x89a_0
            friendlist.addFriend
            ProfileService.GroupMngReq
            OidbSvc.oidb_0x758
            MessageSvc.PbSendMsg
            FeedCloudSvr.trpc.feedcloud.commwriter.ComWriter.DoLike
            OidbSvc.0x758
            trpc.o3.ecdh_access.EcdhAccess.SsoSecureA2Establish
            FeedCloudSvr.trpc.feedcloud.commwriter.ComWriter.DoPush
            qidianservice.290
            trpc.qlive.relationchain_svr.RelationchainSvr.Follow
            trpc.o3.ecdh_access.EcdhAccess.SsoSecureAccess
            FeedCloudSvr.trpc.feedcloud.commwriter.ComWriter.DoFollow
            SQQzoneSvc.forward
            ConnAuthSvr.sdk_auth_api
            wtlogin.qrlogin
            wtlogin.register
            OidbSvcTrpcTcp.0x6d9_4
            trpc.passwd.manager.PasswdManager.SetPasswd
            friendlist.AddFriendReq
            qidianservice.207
            ProfileService.getGroupInfoReq
            OidbSvcTrpcTcp.0x1107_1
            OidbSvcTrpcTcp.0x1105_1
            SQQzoneSvc.publishmood
            wtlogin.exchange_emp
            OidbSvc.0x88d_0
            wtlogin_device.login
            OidbSvcTrpcTcp.0xfa5_1
            trpc.qqhb.qqhb_proxy.Handler.sso_handle
            OidbSvcTrpcTcp.0xf89_1
            OidbSvc.0x9fa
            FeedCloudSvr.trpc.feedcloud.commwriter.ComWriter.PublishFeed
            QChannelSvr.trpc.qchannel.commwriter.ComWriter.PublishFeed
            OidbSvcTrpcTcp.0xf57_106
            ConnAuthSvr.sdk_auth_api_emp
            OidbSvcTrpcTcp.0xf6e_1
            trpc.qlive.word_svr.WordSvr.NewPublicChat
            trpc.passwd.manager.PasswdManager.VerifyPasswd
            trpc.group_pro.msgproxy.sendmsg
            OidbSvc.0x89b_1
            OidbSvcTrpcTcp.0xf57_9
            FeedCloudSvr.trpc.videocircle.circleprofile.CircleProfile.SetProfile
            OidbSvc.0x6d9_4
            OidbSvcTrpcTcp.0xf55_1
            ConnAuthSvr.fast_qq_login
            OidbSvcTrpcTcp.0xf57_1
            trpc.o3.ecdh_access.EcdhAccess.SsoEstablishShareKey
            wtlogin.trans_emp
            """.trim().split("\n").map { it.trim() }

        @JvmStatic
        internal val logger: MiraiLogger = MiraiLogger.Factory.create(UnidbgFetchQsign::class)
    }

    private fun sign0(uin: Long, cmd: String, seq: Int, buffer: ByteArray): Sign {
        println("sign0")
        return sign(uin.toString(), cmd, seq, buffer)
    }

    private fun requestToken0() {
        println("requestToken0")
        QQSecuritySign.requestToken()
    }



    private fun submit(cmd: String, callbackId: Long, buffer: ByteArray) {
        //TODO 0为意淫的
        ChannelManager.onNativeReceive(cmd, buffer, true, 0, callbackId)
    }

    private fun callback(uin: Long, request: List<SsoPacket>) {
        launch(CoroutineName("SendMessage")) {
            for (callback in request) {
                logger.verbose("Bot(${uin}) sendMessage ${callback.cmd} ")
                val result = channel.sendMessage(
                    remark = "mobileqq.msf.security",
                    commandName = callback.cmd,
                    uin = 0,
                    data = callback.body.hex2ByteArray()
                )
                if (result == null) {
                    logger.debug("${callback.cmd} ChannelResult is null")
                    continue
                }

                submit(cmd = result.cmd, callbackId = callback.callbackId, buffer = result.data)
            }
        }
    }

    override fun qSecurityGetSign(
        context: EncryptServiceContext,
        sequenceId: Int,
        commandName: String,
        payload: ByteArray
    ): EncryptService.SignResult? {
        if (commandName == "StatSvc.register") {
            if (token.compareAndSet(0, context.id)) {
                launch(CoroutineName("RequestToken")) {
                    val uin = token.get()
                    while (isActive) {
                        val interval = 2400000L
                        delay(interval)
                        try {
                            requestToken0()
                        } catch (cause: Throwable) {
                            logger.error(cause)
                            continue
                        }
                        callback(uin, Starter.global["PACKET"] as ArrayList<SsoPacket>)
                        (Starter.global["PACKET"] as ArrayList<SsoPacket>).clear()
                    }
                }
            }
        }

        println("commandName:$commandName")
        println(CMD_WHITE_LIST)

        if (commandName !in CMD_WHITE_LIST) return null

        val data = sign0(uin = context.id, cmd = commandName, seq = sequenceId, buffer = payload)
        callback(context.id, Starter.global["PACKET"] as ArrayList<SsoPacket>)
        (Starter.global["PACKET"] as ArrayList<SsoPacket>).clear()
        println("qSecurityGetSign: $data")
        return EncryptService.SignResult(
            sign = data.sign.hex2ByteArray(),
            token = data.token.hex2ByteArray(),
            extra = data.extra.hex2ByteArray()
        )
    }
}