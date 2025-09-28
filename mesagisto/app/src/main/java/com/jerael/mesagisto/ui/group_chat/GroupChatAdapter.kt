package com.jerael.mesagisto.ui.group_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.CURRENT_UID
import com.jerael.mesagisto.database.deleteGroupChatMessageInDb
import com.jerael.mesagisto.database.editGroupChatMessageInDb
import com.jerael.mesagisto.ui.messages.view_holders.AppHolderFactory
import com.jerael.mesagisto.ui.messages.view_holders.MessageHolder
import com.jerael.mesagisto.ui.messages.views.MessageView
import com.jerael.mesagisto.ui.messages.views.TextMessageView
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.showDeleteMessageDialog
import com.jerael.mesagisto.utils.showEditTextMessageDialog
import com.jerael.mesagisto.utils.showToast


class GroupChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessagesCache = mutableListOf<MessageView>()
    private var listHolders = mutableListOf<MessageHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return listMessagesCache[position].getTypeView()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(listMessagesCache[position])

        if (listMessagesCache[position].sender == CURRENT_UID) {
            holder.itemView.setOnLongClickListener {
                showDeleteMessageDialog {
                    deleteGroupChatMessageInDb(
                        listMessagesCache[position].id,
                        GroupChatFragment.groupId
                    ) {
                        showToast(APP_ACTIVITY.getString(R.string.chat_toast_message_deleted))
                        listMessagesCache.removeAt(position)
                        notifyDataSetChanged()
                    }
                }
                true
            }

            if (listMessagesCache[position].text.isNotEmpty()) {
                holder.itemView.setOnClickListener {
                    showEditTextMessageDialog(listMessagesCache[position].text) {
                        editGroupChatMessageInDb(
                            listMessagesCache[position].id,
                            GroupChatFragment.groupId,
                            it
                        ) {
                            showToast(APP_ACTIVITY.getString(R.string.chat_toast_message_changed))
                            (listMessagesCache[position] as TextMessageView).setText(it)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(listMessagesCache[holder.bindingAdapterPosition])
        listHolders.add((holder as MessageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        listHolders.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int = listMessagesCache.size

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            notifyItemInserted(listMessagesCache.size)
        }

        listMessagesCache.sortBy { it.timestamp }

        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            listMessagesCache.sortBy { it.timestamp }
            notifyItemInserted(0)
        }

        onSuccess()
    }

    fun onDestroy() {
        listHolders.forEach {
            it.onDetach()
        }
    }
}

