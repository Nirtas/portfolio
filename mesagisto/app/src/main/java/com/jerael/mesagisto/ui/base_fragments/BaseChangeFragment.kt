package com.jerael.mesagisto.ui.base_fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_change_login.*

open class BaseChangeFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }

        APP_ACTIVITY.binding.navView.visibility = View.GONE

        changes_submit_floating_action_button.setOnClickListener {
            saveChanges()
        }
    }

    open fun saveChanges() {}

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}