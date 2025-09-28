package com.jerael.mesagisto.ui.messages.views

import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.utils.MESSAGE_TYPE_FILE
import com.jerael.mesagisto.utils.MESSAGE_TYPE_IMAGE
import com.jerael.mesagisto.utils.MESSAGE_TYPE_VOICE

class AppViewFactory {
    companion object {
        fun getView(message: CommonModel): MessageView {
            return when (message.type) {
                MESSAGE_TYPE_IMAGE -> ImageMessageView(
                    message.id,
                    message.sender,
                    message.timestamp.toString(),
                    message.fileUrl,
                    fullname = message.fullname,
                    userPhoto = message.photoUrl
                )
                MESSAGE_TYPE_VOICE -> VoiceMessageView(
                    message.id,
                    message.sender,
                    message.timestamp.toString(),
                    message.fileUrl,
                    fullname = message.fullname,
                    userPhoto = message.photoUrl
                )
                MESSAGE_TYPE_FILE -> FileMessageView(
                    message.id,
                    message.sender,
                    message.timestamp.toString(),
                    message.fileUrl,
                    message.fileName,
                    fullname = message.fullname,
                    userPhoto = message.photoUrl
                )
                else -> TextMessageView(
                    message.id,
                    message.sender,
                    message.timestamp.toString(),
                    text = message.text,
                    fullname = message.fullname,
                    userPhoto = message.photoUrl
                )
            }
        }
    }
}