package com.jerael.mesagisto.ui.messages.views

interface MessageView {
    val id: String
    val sender: String
    val timestamp: String
    val fileUrl: String
    val fileName: String
    val text: String
    val fullname: String
    val userPhoto: String

    companion object {
        val MESSAGE_TEXT: Int
            get() = 0

        val MESSAGE_IMAGE: Int
            get() = 1

        val MESSAGE_VOICE: Int
            get() = 2

        val MESSAGE_FILE: Int
            get() = 3
    }

    fun getTypeView(): Int
}