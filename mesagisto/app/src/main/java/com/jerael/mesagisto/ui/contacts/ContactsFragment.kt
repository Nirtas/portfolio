package com.jerael.mesagisto.ui.contacts

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.*
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.item_contact.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var refContacts: DatabaseReference
    private lateinit var refUsers: DatabaseReference
    private lateinit var refUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CoroutineScope(Dispatchers.IO).launch {
            initContacts()
        }

        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        APP_ACTIVITY.binding.navView.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.contacts_title)
        initRecyclerView()
    }

    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.contacts_fullname_text_view
        val photo: CircleImageView = view.contacts_user_photo_circle_image_view
    }

    private fun initRecyclerView() {
        recyclerView = contacts_recycler_view
        refContacts = REF_DATABASE_ROOT.child(DATABASE_NODE_PHONE_CONTACTS).child(CURRENT_UID)

        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(refContacts, CommonModel::class.java)
            .build()

        adapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_contact, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                refUsers = REF_DATABASE_ROOT.child(DATABASE_NODE_USERS).child(model.id)

                refUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()
                    if (contact.fullname.isEmpty()) {
                        holder.name.text = model.fullname
                    } else {
                        holder.name.text = contact.fullname
                    }
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("recipientId", model.id)
                        findNavController().navigate(R.id.navigation_chat, bundle)
                    }
                }

                refUsers.addValueEventListener(refUsersListener)
                mapListeners[refUsers] = refUsersListener
            }
        }

        recyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()

        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                APP_ACTIVITY,
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }
}