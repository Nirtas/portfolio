package com.jerael.mesagisto.ui.main_list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_main_list.view.*

class MainListHolder(view: View): RecyclerView.ViewHolder(view) {
    val userPhoto: CircleImageView = view.main_list_user_photo_circle_image_view
    val name: TextView = view.main_list_fullname_text_view
    val lastMessage: TextView = view.main_list_last_message_text_view
}