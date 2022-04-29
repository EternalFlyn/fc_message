package com.flyn.fc_message.base

import io.netty.buffer.ByteBuf

class RawMessage(val code: Byte, val message: ByteBuf)