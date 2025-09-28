package com.jerael.mesagisto.ui.change_login

import androidx.navigation.fragment.findNavController
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.USER
import com.jerael.mesagisto.database.changeLogin
import com.jerael.mesagisto.database.searchLoginInDb
import com.jerael.mesagisto.ui.base_fragments.BaseChangeFragment
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.showToast
import kotlinx.android.synthetic.main.fragment_change_login.*
import java.util.*

class ChangeLoginFragment : BaseChangeFragment(R.layout.fragment_change_login) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.change_login_title)
        initLogin()
    }

    private fun initLogin() {
        change_login_login_edit_text?.setText(USER.login)
    }

    override fun saveChanges() {
        val newLogin =
            change_login_login_edit_text.text.toString().toLowerCase(Locale.getDefault())

        checkFields(newLogin) {
            searchLoginInDb(newLogin) {
                if (it) {
                    showToast(getString(R.string.change_login_toast_login_already_in_use))
                } else {
                    changeLogin(newLogin) {
                        showToast(getString(R.string.changes_toast_successful_update))
                        USER.login = newLogin
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun checkFields(login: String, function: () -> Unit) {
        when {
            login.isEmpty() -> {
                showToast(getString(R.string.changes_toast_empty_required_fields))
            }
            login.contains(" ") -> {
                showToast(getString(R.string.changes_toast_spaces_in_inputs))
            }
            else -> function()
        }
    }
}