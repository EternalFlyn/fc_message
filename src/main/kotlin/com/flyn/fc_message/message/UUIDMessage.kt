package com.flyn.fc_message.message

import com.flyn.fc_message.base.Coder
import com.flyn.fc_message.base.MessageManager
import com.flyn.fc_message.base.RawMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.util.*

open class UUIDMessage(_uuid: UUID = UUID(0, 0)): Message {

    var uuid = _uuid
        internal set

    companion object: Coder<UUIDMessage> {

        override fun encoder(ctx: ChannelHandlerContext, msg: UUIDMessage): RawMessage {
            with (msg) {
                val buffer = ctx.alloc().buffer()
                buffer.writeLong(uuid.mostSignificantBits)
                buffer.writeLong(uuid.leastSignificantBits)
                return RawMessage(MessageManager.getCode(this@Companion) ,buffer)
            }
        }

        override fun decoder(raw: ByteBuf): UUIDMessage {
            with (raw) {
                val most = readLong()
                val least = readLong()
                return UUIDMessage().apply {
                    uuid = UUID(most, least)
                }
            }
        }

    }

}