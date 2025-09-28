package com.jerael.mesagisto.ui.messages.views

data class TextMessageView(
    override val id: String,
    override val sender: String,
    override val timestamp: String,
    override val fileUrl: String = "",
    override val fileName: String = "",
    override var text: String,
    override val fullname: String = "",
    override val userPhoto: String = ""
) : MessageView {
    override fun getTypeView(): Int {
        return MessageView.MESSAGE_TEXT
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }

    @JvmName("setText1")
    fun setText(newText: String) {
        text = newText
    }

}