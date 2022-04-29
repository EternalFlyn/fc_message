package com.flyn.fc_message.base

import com.flyn.fc_message.message.FileMessage
import com.flyn.fc_message.message.UUIDMessage

object MessageManager {

    private val messages = setOf<Coder<*>>(
        UUIDMessage.Companion,
        FileMessage.Companion
    )

    fun getCode(coder: Coder<*>): Byte {
        return messages.indexOf(coder).toByte()
    }

    fun getMessageClass(code: Byte): Coder<*>? {
        return messages.elementAtOrNull(code.toInt())
    }

}