package com.jerael.mesagisto.ui.messages.views

data class VoiceMessageView(
    override val id: String,
    override val sender: String,
    override val timestamp: String,
    override val fileUrl: String,
    override val fileName: String = "",
    override val text: String = "",
    override val fullname: String = "",
    override val userPhoto: String = ""
) : MessageView {
    override fun getTypeView(): Int {
        return MessageView.MESSAGE_VOICE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }

}