package com.jerael.mesagisto.ui.messages.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jerael.mesagisto.database.CURRENT_UID
import com.jerael.mesagisto.database.getFileFromStorage
import com.jerael.mesagisto.ui.group_chat.GroupChatFragment
import com.jerael.mesagisto.ui.messages.views.MessageView
import com.jerael.mesagisto.utils.*
import kotlinx.android.synthetic.main.item_chat_message_file.view.*
import java.io.File


class FileMessageHolder(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val chatMessageMyMessageFileConstraintLayout: ConstraintLayout =
        view.chat_message_my_message_file_constraint_layout
    private val chatMessageMyMessageFileButton: ImageView = view.chat_message_my_message_file_button
    private val chatMessageMyMessageFileName: TextView = view.chat_message_my_message_file_name
    private val chatMessageMyMessageFileProgressBar: ProgressBar =
        view.chat_message_my_message_file_progress_bar
    private val chatMessageMyMessageFileTime: TextView =
        view.chat_message_my_message_file_time_text_view

    private val chatMessageYourMessageFile: ConstraintLayout =
        view.chat_message_your_message_file
    private val chatMessageYourMessageFileUserPhoto: ImageView =
        view.chat_message_your_message_file_user_photo
    private val chatMessageYourMessageFileFullname: TextView =
        view.chat_message_your_message_file_fullname
    private val chatMessageYourMessageFileButton: ImageView =
        view.chat_message_your_message_file_button
    private val chatMessageYourMessageFileName: TextView = view.chat_message_your_message_file_name
    private val chatMessageYourMessageFileProgressBar: ProgressBar =
        view.chat_message_your_message_file_progress_bar
    private val chatMessageYourMessageFileTime: TextView =
        view.chat_message_your_message_file_time_text_view

    override fun drawMessage(view: MessageView) {
        if (view.sender == CURRENT_UID) {
            chatMessageMyMessageFileConstraintLayout.visibility = View.VISIBLE
            chatMessageMyMessageFileTime.text = view.timestamp.toTimeFormat()
            chatMessageMyMessageFileName.text = view.fileName

            chatMessageYourMessageFile.visibility = View.GONE

        } else {
            chatMessageMyMessageFileConstraintLayout.visibility = View.GONE

            chatMessageYourMessageFile.visibility = View.VISIBLE
            chatMessageYourMessageFileName.text = view.fileName
            chatMessageYourMessageFileTime.text = view.timestamp.toTimeFormat()
            if (GroupChatFragment.groupId != "") {
                chatMessageYourMessageFileUserPhoto.downloadAndSetImage(view.userPhoto)
                chatMessageYourMessageFileFullname.text = view.fullname
            } else {
                chatMessageYourMessageFileUserPhoto.visibility = View.GONE
                chatMessageYourMessageFileFullname.visibility = View.GONE
            }

        }
    }

    override fun onAttach(view: MessageView) {
        if (view.sender == CURRENT_UID) {
            chatMessageMyMessageFileButton.setOnClickListener {
                clickOnFileButton(view)
            }
        } else {
            chatMessageYourMessageFileButton.setOnClickListener {
                clickOnFileButton(view)
            }
        }
    }

    private fun clickOnFileButton(view: MessageView) {
        if (view.sender == CURRENT_UID) {
            chatMessageMyMessageFileButton.visibility = View.INVISIBLE
            chatMessageMyMessageFileProgressBar.visibility = View.VISIBLE
        } else {
            chatMessageYourMessageFileButton.visibility = View.INVISIBLE
            chatMessageYourMessageFileProgressBar.visibility = View.VISIBLE
        }

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.fileName
        )

        try {
            if (checkPermission(WRITE_FILES)) {
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl) {
                    if (view.sender == CURRENT_UID) {
                        chatMessageMyMessageFileButton.visibility = View.VISIBLE
                        chatMessageMyMessageFileProgressBar.visibility = View.INVISIBLE
                    } else {
                        chatMessageYourMessageFileButton.visibility = View.VISIBLE
                        chatMessageYourMessageFileProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    override fun onDetach() {
        chatMessageMyMessageFileButton.setOnClickListener(null)
        chatMessageYourMessageFileButton.setOnClickListener(null)
    }
}