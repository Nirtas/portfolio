package com.jerael.mesagisto.ui.group_add_user

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_group_add_user.view.*

class AddUserHolder(view: View): RecyclerView.ViewHolder(view) {
    val userPhoto: CircleImageView = view.group_add_user_user_photo_circle_image_view
    val name: TextView = view.group_add_user_fullname_text_view
    val check: ImageView = view.group_add_user_check
}