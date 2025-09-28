package com.jerael.mesagisto.ui.messages.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jerael.mesagisto.database.CURRENT_UID
import com.jerael.mesagisto.ui.group_chat.GroupChatFragment
import com.jerael.mesagisto.ui.messages.views.MessageView
import com.jerael.mesagisto.utils.downloadAndSetImage
import com.jerael.mesagisto.utils.toTimeFormat
import kotlinx.android.synthetic.main.item_chat_message_text.view.*

class TextMessageHolder(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val chatMessageMyMessageConstraintLayout: ConstraintLayout =
        view.chat_message_my_message_constraint_layout
    private val chatMessageMyMessageText: TextView = view.chat_message_my_message_text_text_view
    private val chatMessageMyMessageTime: TextView = view.chat_message_my_message_time_text_view

    private val chatMessageYourMessageText: ConstraintLayout =
        view.chat_message_your_message_text
    private val chatMessageYourMessageTextUserPhoto: ImageView = view.chat_message_your_message_text_user_photo
    private val chatMessageYourMessageTextFullname: TextView = view.chat_message_your_message_text_fullname
    private val chatMessageYourMessageTextTextView: TextView = view.chat_message_your_message_text_text_view
    private val chatMessageYourMessageTime: TextView = view.chat_message_your_message_time_text_view

    override fun drawMessage(view: MessageView) {
        if (view.sender == CURRENT_UID) {
            chatMessageMyMessageConstraintLayout.visibility = View.VISIBLE
            chatMessageMyMessageText.text = view.text
            chatMessageMyMessageTime.text = view.timestamp.toTimeFormat()

            chatMessageYourMessageText.visibility = View.GONE
        } else {
            chatMessageMyMessageConstraintLayout.visibility = View.GONE

            chatMessageYourMessageText.visibility = View.VISIBLE
            chatMessageYourMessageTextTextView.text = view.text
            chatMessageYourMessageTime.text = view.timestamp.toTimeFormat()
            if (GroupChatFragment.groupId != "") {
                chatMessageYourMessageTextUserPhoto.downloadAndSetImage(view.userPhoto)
                chatMessageYourMessageTextFullname.text = view.fullname
            } else {
                chatMessageYourMessageTextUserPhoto.visibility = View.GONE
                chatMessageYourMessageTextFullname.visibility = View.GONE
            }
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}