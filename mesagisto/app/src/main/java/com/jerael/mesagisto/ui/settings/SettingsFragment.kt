package com.jerael.mesagisto.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.*
import com.jerael.mesagisto.utils.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        APP_ACTIVITY.binding.navView.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        APP_ACTIVITY.title = getString(R.string.settings_title)
        init()

        settings_account_login_constraint_layout.setOnClickListener {
            findNavController().navigate(R.id.navigation_change_login)
        }

        settings_account_info_constraint_layout.setOnClickListener {
            findNavController().navigate(R.id.navigation_change_user_info)
        }

        settings_other_theme_switcher.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                AppThemeUtil.setIsDarkThemeEnabled(APP_ACTIVITY, true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                AppThemeUtil.setIsDarkThemeEnabled(APP_ACTIVITY, false)
            }

            showToast(getString(R.string.settings_label_theme_changed))
            restartActivity()
        }
    }

    private fun init() {
        settings_full_name_text_view.text = USER.fullname
        settings_account_phone_number_text_view.text = USER.phone
        settings_account_login_text_view.text = USER.login
        settings_user_photo_circle_image_view.downloadAndSetImage(USER.photoUrl)
        settings_other_theme_switcher.isChecked = AppThemeUtil.isDarkThemeEnabled(APP_ACTIVITY)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        APP_ACTIVITY.menuInflater.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.change_fullname -> {
                findNavController().navigate(R.id.navigation_change_fullname)
                true
            }
            R.id.change_user_photo -> {
                changePhoto()
                true
            }
            R.id.exit -> {
                signOut()
                restartActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changePhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data)?.uriContent

            if (uri != null) {
                val path = REF_STORAGE_ROOT.child(STORAGE_FOLDER_PROFILE_IMAGE).child(CURRENT_UID)

                putFileToStorage(uri, path) {
                    getUrlFromStorage(path) {
                        putUrlToDb(it) {
                            showToast(getString(R.string.changes_toast_successful_update))
                            settings_user_photo_circle_image_view.downloadAndSetImage(uri.toString())
                            USER.photoUrl = it
                        }
                    }
                }
            }
        }
    }
}