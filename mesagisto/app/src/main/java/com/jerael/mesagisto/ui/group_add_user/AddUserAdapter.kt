package com.jerael.mesagisto.ui.group_add_user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jerael.mesagisto.R
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.utils.downloadAndSetImage

class AddUserAdapter: RecyclerView.Adapter<AddUserHolder>() {

    private val listItems = mutableListOf<CommonModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddUserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_add_user, parent, false)
        val holder = AddUserHolder(view)
        holder.itemView.setOnClickListener {
            if (listItems[holder.bindingAdapterPosition].check) {
                holder.check.visibility = View.INVISIBLE
                listItems[holder.bindingAdapterPosition].check = false
                AddUserFragment.listUsers.remove(listItems[holder.bindingAdapterPosition])
            } else {
                holder.check.visibility = View.VISIBLE
                listItems[holder.bindingAdapterPosition].check = true
                AddUserFragment.listUsers.add(listItems[holder.bindingAdapterPosition])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: AddUserHolder, position: Int) {
        holder.userPhoto.downloadAndSetImage(listItems[position].photoUrl)
        holder.name.text = listItems[position].fullname
    }

    override fun getItemCount(): Int = listItems.size

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}