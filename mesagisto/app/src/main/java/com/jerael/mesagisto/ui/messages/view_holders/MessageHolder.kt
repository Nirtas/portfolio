package com.jerael.mesagisto.ui.messages.view_holders

import com.jerael.mesagisto.ui.messages.views.MessageView

interface MessageHolder {
    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)
    fun onDetach()
}