package com.jerael.mesagisto.ui.group_add_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.*
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.AppValueEventListener
import com.jerael.mesagisto.utils.showToast
import kotlinx.android.synthetic.main.fragment_add_user.*

class AddUserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddUserAdapter
    private val refContacts =
        REF_DATABASE_ROOT.child(DATABASE_NODE_PHONE_CONTACTS).child(CURRENT_UID)
    private val refUsers = REF_DATABASE_ROOT.child(DATABASE_NODE_USERS)
    private var listItems = listOf<CommonModel>()

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
        return inflater.inflate(R.layout.fragment_add_user, container, false)
    }

    override fun onResume() {
        super.onResume()
        listUsers.clear()
        APP_ACTIVITY.title = getString(R.string.group_add_user_title)
        initRecyclerView()
        group_next_floating_action_button.setOnClickListener {
            if (listUsers.size > 0) {
                val jsonUsers = Klaxon().toJsonString(listUsers)

                val bundle = Bundle()
                bundle.putString("users", jsonUsers)

                findNavController().navigate(R.id.navigation_group_settings, bundle)
            } else {
                showToast(getString(R.string.group_add_user_toast_no_users))
            }
        }
    }

    private fun initRecyclerView() {
        recyclerView = group_add_user_recycler_view
        adapter = AddUserAdapter()

        refContacts.addListenerForSingleValueEvent(AppValueEventListener {
            listItems = it.children.map { dataSnapshot ->
                dataSnapshot.getCommonModel()
            }

            listItems.forEach { model ->
                refUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
                        val newModel = dataSnapshot.getCommonModel()

                        adapter.updateListItems(newModel)
                    })
            }
        })

        recyclerView.adapter = adapter
    }

    companion object {
        val listUsers = mutableListOf<CommonModel>()
    }

}