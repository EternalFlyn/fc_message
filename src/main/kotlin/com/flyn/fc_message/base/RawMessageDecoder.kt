package com.flyn.fc_message.base

import com.flyn.fc_message.message.UUIDMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder
import io.netty.util.ReferenceCountUtil
import java.util.*
import kotlin.properties.Delegates

class RawMessageDecoder: ReplayingDecoder<DecoderState>(DecoderState.MESSAGE_TYPE) {

    private var code by Delegates.notNull<Byte>()
    private var length by Delegates.notNull<Int>()
    private lateinit var uuid: UUID

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        when (state()) {
            DecoderState.MESSAGE_TYPE -> {
                code = `in`.readByte()
                MessageManager.getMessageClass(code)?:run {
                    ctx.channel().close()
                    return
                }
                checkpoint(DecoderState.MESSAGE_LENGTH)
            }
            DecoderState.MESSAGE_LENGTH -> {
                length = `in`.readInt()
                checkpoint(DecoderState.MESSAGE)
            }
            DecoderState.MESSAGE -> {
                val msg = `in`.readBytes(length)
                MessageManager.getMessageClass(code)?.let {
                    try {
                        val message = it.decoder(msg)
                        if (message is UUIDMessage) {
                            if (code == 0.toByte()) uuid = message.uuid
                            else message.uuid = uuid
                        }
                        out.add(message)
                    } catch (exception: Exception) {
                        throw exception
                    }
                }?: throw Error("Don't have this message!")
                ReferenceCountUtil.release(msg)
                checkpoint(DecoderState.MESSAGE_TYPE)
            }
            else -> throw Error("Shouldn't reach here.")
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.channel().close()
    }

}