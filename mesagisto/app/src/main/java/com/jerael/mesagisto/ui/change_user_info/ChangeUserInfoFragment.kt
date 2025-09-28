package com.jerael.mesagisto.ui.change_user_info

import androidx.navigation.fragment.findNavController
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.USER
import com.jerael.mesagisto.database.saveUserInfoInDb
import com.jerael.mesagisto.ui.base_fragments.BaseChangeFragment
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.showToast
import kotlinx.android.synthetic.main.fragment_change_user_info.*

class ChangeUserInfoFragment : BaseChangeFragment(R.layout.fragment_change_user_info) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.change_user_info_title)
        change_user_info_user_info_edit_text?.setText(USER.userInfo)
    }

    override fun saveChanges() {
        super.saveChanges()

        val newUserInfo = change_user_info_user_info_edit_text.text.toString()

        saveUserInfoInDb(newUserInfo) {
            showToast(getString(R.string.changes_toast_successful_update))
            USER.userInfo = newUserInfo
            findNavController().popBackStack()
        }
    }
}