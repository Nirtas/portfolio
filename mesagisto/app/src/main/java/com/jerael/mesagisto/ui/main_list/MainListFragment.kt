package com.jerael.mesagisto.ui.main_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.*
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.utils.*
import kotlinx.android.synthetic.main.fragment_main_list.*

class MainListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainListAdapter
    private lateinit var refMainList: DatabaseReference
    private lateinit var refUsers: DatabaseReference
    private lateinit var refGroups: DatabaseReference
    private lateinit var refMessages: DatabaseReference
    private var listItems = listOf<CommonModel>()

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.to_bottom_anim) }

    private var clicked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        APP_ACTIVITY.binding.navView.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_main_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        main_list_floating_action_button.setOnClickListener {
            onFabClicked()
        }

        create_group_action_button.setOnClickListener {
            findNavController().navigate(R.id.navigation_add_user)
        }
    }

    private fun onFabClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            create_group_action_button.visibility = View.VISIBLE
        } else {
            create_group_action_button.visibility = View.INVISIBLE
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            create_group_action_button.startAnimation(fromBottom)
            main_list_floating_action_button.startAnimation(rotateOpen)
        } else {
            create_group_action_button.startAnimation(toBottom)
            main_list_floating_action_button.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        create_group_action_button.isClickable = !clicked
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.main_list_title)
        initRecyclerView()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun initRecyclerView() {
        recyclerView = main_list_recycler_view
        adapter = MainListAdapter()

        refMainList = REF_DATABASE_ROOT.child(DATABASE_NODE_MAIN_LIST).child(CURRENT_UID)
        refMainList.addListenerForSingleValueEvent(AppValueEventListener {

            listItems = it.children.map { dataSnapshot ->
                dataSnapshot.getCommonModel()
            }

            listItems.forEach { model ->
                when (model.type) {
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }
            }
        })

        recyclerView.adapter = adapter
    }

    private fun showGroup(model: CommonModel) {
        refGroups = REF_DATABASE_ROOT.child(DATABASE_NODE_GROUPS)
        refGroups.child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
                val newModel = dataSnapshot.getCommonModel()

                refGroups.child(model.id).child(DATABASE_NODE_MESSAGES).limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val tempList =
                            dataSnapshot2.children.map { dataSnapshot3 -> dataSnapshot3.getCommonModel() }

                        if (tempList.isNotEmpty()) {
                            val sender =
                                if (tempList[0].sender == CURRENT_UID)
                                    "${APP_ACTIVITY.getString(R.string.main_list_last_message_sender_me)}: "
                                else
                                    ""

                            newModel.lastMessage = sender + when (tempList[0].type) {
                                MESSAGE_TYPE_TEXT -> tempList[0].text
                                MESSAGE_TYPE_IMAGE -> APP_ACTIVITY.getString(R.string.main_list_last_message_image)
                                MESSAGE_TYPE_FILE -> APP_ACTIVITY.getString(R.string.main_list_last_message_file)
                                MESSAGE_TYPE_VOICE -> APP_ACTIVITY.getString(R.string.main_list_last_message_voice)
                                else -> ""
                            }
                        } else {
                            newModel.lastMessage =
                                APP_ACTIVITY.getString(R.string.main_list_last_message_no_messages)
                        }

                        newModel.type = TYPE_GROUP

                        adapter.updateListItems(newModel)
                    })
            })
    }

    private fun showChat(model: CommonModel) {

        refUsers = REF_DATABASE_ROOT.child(DATABASE_NODE_USERS)
        refUsers.child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
                val newModel = dataSnapshot.getCommonModel()

                refMessages = REF_DATABASE_ROOT.child(DATABASE_NODE_MESSAGES).child(CURRENT_UID)
                refMessages.child(model.id).limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val tempList =
                            dataSnapshot2.children.map { dataSnapshot3 -> dataSnapshot3.getCommonModel() }

                        if (tempList.isNotEmpty()) {
                            val sender =
                                if (tempList[0].sender == CURRENT_UID)
                                    "${APP_ACTIVITY.getString(R.string.main_list_last_message_sender_me)}: "
                                else
                                    ""

                            newModel.lastMessage = sender + when (tempList[0].type) {
                                MESSAGE_TYPE_TEXT -> tempList[0].text
                                MESSAGE_TYPE_IMAGE -> APP_ACTIVITY.getString(R.string.main_list_last_message_image)
                                MESSAGE_TYPE_FILE -> APP_ACTIVITY.getString(R.string.main_list_last_message_file)
                                MESSAGE_TYPE_VOICE -> APP_ACTIVITY.getString(R.string.main_list_last_message_voice)
                                else -> ""
                            }
                        } else {
                            newModel.lastMessage =
                                APP_ACTIVITY.getString(R.string.main_list_last_message_no_messages)
                        }

                        if (newModel.fullname.isEmpty()) {
                            newModel.fullname = newModel.phone
                        }

                        newModel.type = TYPE_CHAT

                        adapter.updateListItems(newModel)
                    })
            })
    }
}