package com.jerael.mesagisto.ui.main_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jerael.mesagisto.R
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.TYPE_CHAT
import com.jerael.mesagisto.utils.TYPE_GROUP
import com.jerael.mesagisto.utils.downloadAndSetImage

class MainListAdapter : RecyclerView.Adapter<MainListHolder>() {

    private val listItems = mutableListOf<CommonModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_main_list, parent, false)
        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            when (listItems[holder.bindingAdapterPosition].type) {
                TYPE_CHAT -> {
                    val bundle = Bundle()
                    bundle.putString("recipientId", listItems[holder.bindingAdapterPosition].id)
                    APP_ACTIVITY.supportFragmentManager.fragments[0].findNavController()
                        .navigate(R.id.navigation_chat, bundle)
                }
                TYPE_GROUP -> {
                    val bundle = Bundle()
                    bundle.putString("groupId", listItems[holder.bindingAdapterPosition].id)
                    APP_ACTIVITY.supportFragmentManager.fragments[0].findNavController()
                        .navigate(R.id.navigation_group_chat, bundle)
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.userPhoto.downloadAndSetImage(listItems[position].photoUrl)
        holder.name.text = listItems[position].fullname
        holder.lastMessage.text = listItems[position].lastMessage
    }

    override fun getItemCount(): Int = listItems.size

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}