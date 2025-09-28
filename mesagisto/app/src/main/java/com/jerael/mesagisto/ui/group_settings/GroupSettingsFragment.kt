package com.jerael.mesagisto.ui.group_settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.saveGroupInDb
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.ui.group_add_user.AddUserAdapter
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.restartActivity
import com.jerael.mesagisto.utils.showToast
import kotlinx.android.synthetic.main.fragment_group_settings.*

class GroupSettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddUserAdapter
    private var uri = Uri.EMPTY
    private lateinit var listUsers: List<CommonModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }

        APP_ACTIVITY.binding.navView.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_group_settings, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jsonUsers = arguments?.getString("users") ?: "[]"
        listUsers = Klaxon().parseArray(jsonUsers) ?: emptyList()
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.group_settings_title)
        if (listUsers.isNotEmpty()) {
            initRecyclerView()
            settings_group_photo_circle_image_view.setOnClickListener { changePhoto() }
            settings_group_floating_action_button.setOnClickListener {
                val groupName = settings_group_name.text.toString()
                if (groupName.isEmpty()) {
                    showToast(getString(R.string.changes_toast_empty_required_fields))
                } else {
                    if (uri == Uri.EMPTY) {
                        showToast(getString(R.string.group_settings_toast_choose_pic))
                    } else {
                        saveGroupInDb(groupName, uri, listUsers) {
                            restartActivity()
                        }
                    }
                }
            }
        } else {
            showToast(getString(R.string.group_add_user_toast_no_users))
            findNavController().popBackStack()
        }
    }

    private fun changePhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        recyclerView = settings_group_users_recycler_view
        adapter = AddUserAdapter()
        recyclerView.adapter = adapter
        listUsers.forEach { adapter.updateListItems(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            uri = CropImage.getActivityResult(data)?.uriContent
            settings_group_photo_circle_image_view.setImageURI(uri)
        }
    }

}