package com.jerael.mesagisto.ui.messages.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jerael.mesagisto.R
import com.jerael.mesagisto.ui.messages.views.MessageView

class AppHolderFactory {
    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_chat_message_image, parent, false)
                    ImageMessageHolder(view)
                }
                MessageView.MESSAGE_VOICE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_chat_message_voice, parent, false)
                    VoiceMessageHolder(view)
                }
                MessageView.MESSAGE_FILE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_chat_message_file, parent, false)
                    FileMessageHolder(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_chat_message_text, parent, false)
                    TextMessageHolder(view)
                }
            }
        }
    }
}