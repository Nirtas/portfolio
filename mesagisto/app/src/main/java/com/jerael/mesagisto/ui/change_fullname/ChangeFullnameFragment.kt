package com.jerael.mesagisto.ui.change_fullname

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.*
import com.jerael.mesagisto.ui.base_fragments.BaseChangeFragment
import com.jerael.mesagisto.ui.registration.PhoneVerificationFragment
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.restartActivity
import com.jerael.mesagisto.utils.showToast
import kotlinx.android.synthetic.main.fragment_change_fullname.*


class ChangeFullnameFragment : BaseChangeFragment(R.layout.fragment_change_fullname) {

    private var isRegistration: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRegistration = arguments?.getBoolean("isRegistration") ?: false
    }

    override fun onResume() {
        super.onResume()

        APP_ACTIVITY.title = getString(R.string.change_fullname_title)

        if (!isRegistration) {
            initFullname()
        }
    }

    private fun initFullname() {
        val fullnameList = USER.fullname.split(" ")
        change_fullname_name_edit_text?.setText(fullnameList.getOrNull(0))
        change_fullname_surname_edit_text?.setText(fullnameList.getOrNull(1))
    }

    override fun saveChanges() {
        val name = change_fullname_name_edit_text.text.toString()
        val surname = change_fullname_surname_edit_text.text.toString()

        checkFields(name, surname) {
            var fullname = name

            if (surname.isNotEmpty()) {
                fullname += " $surname"
            }

            if (isRegistration) {
                saveUserInDb(PhoneVerificationFragment.phone) {
                    CURRENT_UID = AUTH.currentUser?.uid.toString()
                    saveFullnameInDb(fullname) {
                        restartActivity()
                    }
                }
            } else {
                saveFullnameInDb(fullname) {
                    showToast(getString(R.string.changes_toast_successful_update))
                    USER.fullname = fullname
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun checkFields(name: String, surname: String, function: () -> Unit) {
        when {
            name.isEmpty() -> {
                showToast(getString(R.string.changes_toast_empty_required_fields))
            }
            name.contains(" ") || surname.contains(" ") -> {
                showToast(getString(R.string.changes_toast_spaces_in_inputs))
            }
            else -> function()
        }
    }
}