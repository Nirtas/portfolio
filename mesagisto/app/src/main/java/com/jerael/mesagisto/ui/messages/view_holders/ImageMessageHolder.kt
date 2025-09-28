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
import kotlinx.android.synthetic.main.item_chat_message_image.view.*

class ImageMessageHolder(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val chatMessageMyMessageImageConstraintLayout: ConstraintLayout =
        view.chat_message_my_message_image_constraint_layout
    private val chatMessageMyMessageImage: ImageView = view.chat_message_my_message_image_image_view
    private val chatMessageMyMessageImageTime: TextView = view.chat_message_my_message_image_time_text_view

    private val chatMessageYourMessageImage: ConstraintLayout =
        view.chat_message_your_message_image
    private val chatMessageYourMessageImageUserPhoto: ImageView = view.chat_message_your_message_image_user_photo
    private val chatMessageYourMessageImageFullname: TextView =
        view.chat_message_your_message_image_fullname
    private val chatMessageYourMessageImageImageView: ImageView = view.chat_message_your_message_image_image_view
    private val chatMessageYourMessageImageTime: TextView =
        view.chat_message_your_message_image_time_text_view

    override fun drawMessage(view: MessageView) {
        if (view.sender == CURRENT_UID) {
            chatMessageMyMessageImageConstraintLayout.visibility = View.VISIBLE
            chatMessageMyMessageImage.downloadAndSetImage(view.fileUrl)
            chatMessageMyMessageImageTime.text = view.timestamp.toTimeFormat()

            chatMessageYourMessageImage.visibility = View.GONE

        } else {
            chatMessageMyMessageImageConstraintLayout.visibility = View.GONE

            chatMessageYourMessageImage.visibility = View.VISIBLE
            chatMessageYourMessageImageImageView.downloadAndSetImage(view.fileUrl)
            chatMessageYourMessageImageTime.text = view.timestamp.toTimeFormat()
            if (GroupChatFragment.groupId != "") {
                chatMessageYourMessageImageUserPhoto.downloadAndSetImage(view.userPhoto)
                chatMessageYourMessageImageFullname.text = view.fullname
            } else {
                chatMessageYourMessageImageUserPhoto.visibility = View.GONE
                chatMessageYourMessageImageFullname.visibility = View.GONE
            }
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}