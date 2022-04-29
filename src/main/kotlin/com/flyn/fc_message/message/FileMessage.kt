package com.flyn.fc_message.message

import com.flyn.fc_message.base.Coder
import com.flyn.fc_message.base.MessageManager
import com.flyn.fc_message.base.RawMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.nio.ByteBuffer
import java.nio.charset.Charset

class FileMessage(val fileName: String, val remaining: Boolean, val data: ByteBuffer) : UUIDMessage() {

    companion object: Coder<FileMessage> {

        private val charset = Charset.forName("UTF-8")

        override fun encoder(ctx: ChannelHandlerContext, msg: FileMessage): RawMessage {
            with (msg) {
                val buffer = ctx.alloc().buffer()
                buffer.writeInt(fileName.length)
                buffer.writeCharSequence(fileName, charset)
                buffer.writeBoolean(remaining)
                buffer.writeInt(data.limit())
                buffer.writeBytes(data)
                return RawMessage(MessageManager.getCode(this@Companion), buffer)
            }
        }

        override fun decoder(raw: ByteBuf): FileMessage {
            with (raw) {
                val fileName = readCharSequence(readInt(), charset)
                val remaining = readBoolean()
                val data = ByteBuffer.allocateDirect(readInt())
                getBytes(readerIndex(), data)
                data.flip()
                return FileMessage(fileName.toString(), remaining, data)
            }
        }

    }

}