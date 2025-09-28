package com.jerael.mesagisto.ui.messages.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jerael.mesagisto.database.CURRENT_UID
import com.jerael.mesagisto.ui.group_chat.GroupChatFragment
import com.jerael.mesagisto.ui.messages.views.MessageView
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.AppVoicePlayer
import com.jerael.mesagisto.utils.downloadAndSetImage
import com.jerael.mesagisto.utils.toTimeFormat
import kotlinx.android.synthetic.main.item_chat_message_voice.view.*

class VoiceMessageHolder(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val appVoicePlayer = AppVoicePlayer()

    private val chatMessageMyMessageVoiceConstraintLayout: ConstraintLayout =
        view.chat_message_my_message_voice_constraint_layout
    private val chatMessageMyMessageVoicePlayButton: ImageView =
        view.chat_message_my_message_voice_play_button
    private val chatMessageMyMessageVoicePauseButton: ImageView =
        view.chat_message_my_message_voice_pause_button
    private val chatMessageMyMessageVoiceTime: TextView =
        view.chat_message_my_message_voice_time_text_view
    private val chatMessageMyMessageVoiceSeekBar: SeekBar =
        view.chat_message_my_message_voice_seek_bar

    private val chatMessageYourMessageVoice: ConstraintLayout =
        view.chat_message_your_message_voice
    private val chatMessageYourMessageVoiceUserPhoto: ImageView =
        view.chat_message_your_message_voice_user_photo
    private val chatMessageYourMessageVoiceFullname: TextView =
        view.chat_message_your_message_voice_fullname
    private val chatMessageYourMessageVoicePlayButton: ImageView =
        view.chat_message_your_message_voice_play_button
    private val chatMessageYourMessageVoicePauseButton: ImageView =
        view.chat_message_your_message_voice_pause_button
    private val chatMessageYourMessageVoiceTime: TextView =
        view.chat_message_your_message_voice_time_text_view
    private val chatMessageYourMessageVoiceSeekBar: SeekBar =
        view.chat_message_your_message_voice_seek_bar

    override fun drawMessage(view: MessageView) {
        if (view.sender == CURRENT_UID) {
            chatMessageMyMessageVoiceConstraintLayout.visibility = View.VISIBLE
            chatMessageMyMessageVoiceTime.text = view.timestamp.toTimeFormat()

            chatMessageYourMessageVoice.visibility = View.GONE

        } else {
            chatMessageMyMessageVoiceConstraintLayout.visibility = View.GONE

            chatMessageYourMessageVoice.visibility = View.VISIBLE
            chatMessageYourMessageVoiceTime.text = view.timestamp.toTimeFormat()
            if (GroupChatFragment.groupId != "") {
                chatMessageYourMessageVoiceUserPhoto.downloadAndSetImage(view.userPhoto)
                chatMessageYourMessageVoiceFullname.text = view.fullname
            } else {
                chatMessageYourMessageVoiceUserPhoto.visibility = View.GONE
                chatMessageYourMessageVoiceFullname.visibility = View.GONE
            }
        }
    }

    override fun onAttach(view: MessageView) {
        appVoicePlayer.init()
        if (view.sender == CURRENT_UID) {

            chatMessageMyMessageVoicePlayButton.setOnClickListener {

                chatMessageMyMessageVoicePlayButton.visibility = View.GONE
                chatMessageMyMessageVoicePauseButton.visibility = View.VISIBLE

                appVoicePlayer.create(APP_ACTIVITY, view.fileUrl, view.id, chatMessageMyMessageVoiceSeekBar) {

                    chatMessageMyMessageVoicePauseButton.setOnClickListener {

                        appVoicePlayer.pause {
                            chatMessageMyMessageVoicePauseButton.setOnClickListener(null)
                            chatMessageMyMessageVoicePlayButton.visibility = View.VISIBLE
                            chatMessageMyMessageVoicePauseButton.visibility = View.GONE
                        }
                    }

                    appVoicePlayer.play {
                        chatMessageMyMessageVoicePlayButton.visibility = View.VISIBLE
                        chatMessageMyMessageVoicePauseButton.visibility = View.GONE
                    }
                }
            }
        } else {
            chatMessageYourMessageVoicePlayButton.setOnClickListener {

                chatMessageYourMessageVoicePlayButton.visibility = View.GONE
                chatMessageYourMessageVoicePauseButton.visibility = View.VISIBLE

                appVoicePlayer.create(APP_ACTIVITY, view.fileUrl, view.id, chatMessageYourMessageVoiceSeekBar) {

                    chatMessageYourMessageVoicePauseButton.setOnClickListener {

                        appVoicePlayer.pause {
                            chatMessageYourMessageVoicePauseButton.setOnClickListener(null)
                            chatMessageYourMessageVoicePlayButton.visibility = View.VISIBLE
                            chatMessageYourMessageVoicePauseButton.visibility = View.GONE
                        }
                    }

                    appVoicePlayer.play {
                        chatMessageYourMessageVoicePlayButton.visibility = View.VISIBLE
                        chatMessageYourMessageVoicePauseButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDetach() {
        chatMessageMyMessageVoicePlayButton.setOnClickListener(null)
        chatMessageYourMessageVoicePlayButton.setOnClickListener(null)
    }
}