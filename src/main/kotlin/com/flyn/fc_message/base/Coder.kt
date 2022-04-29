package com.flyn.fc_message.base

import com.flyn.fc_message.message.Message
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

interface Coder<T: Message> {

    fun encoder(ctx: ChannelHandlerContext ,msg: T): RawMessage
    fun decoder(raw: ByteBuf): T

}