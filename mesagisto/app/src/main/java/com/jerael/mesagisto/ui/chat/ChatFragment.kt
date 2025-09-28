package com.jerael.mesagisto.ui.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.*
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.ui.messages.views.AppViewFactory
import com.jerael.mesagisto.utils.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.toolbar_chat.view.*
import kotlinx.android.synthetic.main.upload_choice.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class ChatFragment : Fragment() {

    private lateinit var listenerInfoToolbar: AppValueEventListener
    private lateinit var toolbarInfo: View
    private lateinit var refUser: DatabaseReference
    private lateinit var refMessages: DatabaseReference
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messagesListener: AppChildEventListener

    //Количество подгружаемых при открытии чата и при скролле сообщений
    private val countUploadedMessages = 15

    //Общее количество подгруженных сообщений
    private var totalCountLoadedMessages = countUploadedMessages

    private var isScrolling = false
    private var isSmoothScrollToPosition = true
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appVoiceRecorder: AppVoiceRecorder
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var userName: String
    private lateinit var userPhotoUrl: String

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
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipientId = arguments?.getString("recipientId") ?: ""
    }

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        setHasOptionsMenu(true)
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_upload_choice)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        appVoiceRecorder = AppVoiceRecorder()
        swipeRefreshLayout = chat_swipe_refresh_layout
        layoutManager = LinearLayoutManager(this.context)
        chat_input_message_edit_text.addTextChangedListener(AppTextWatcher {
            val message = chat_input_message_edit_text.text.toString()
            if (message.isEmpty() || message == getString(R.string.chat_input_message_recording)) {
                chat_send_message_button.visibility = View.GONE
                chat_attach_file_button.visibility = View.VISIBLE
                chat_voice_button.visibility = View.VISIBLE
            } else {
                chat_send_message_button.visibility = View.VISIBLE
                chat_attach_file_button.visibility = View.GONE
                chat_voice_button.visibility = View.GONE
            }
        })

        chat_attach_file_button.setOnClickListener {
            attach()
        }

        CoroutineScope(Dispatchers.IO).launch {
            chat_voice_button.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    //Минимальное время записи - 0,3 мс
                    val minTime = 300

                    if (event.action == MotionEvent.ACTION_DOWN) {
                        //Получаем время начала записи
                        v.tag = System.currentTimeMillis()

                        chat_input_message_edit_text.setText(getString(R.string.chat_input_message_recording))

                        val typedValue = TypedValue()
                        APP_ACTIVITY.theme.resolveAttribute(
                            R.attr.colorDrawableVoiceRecordingPressed,
                            typedValue,
                            true
                        )
                        chat_voice_button.setColorFilter(typedValue.data)

                        val messageKey = getMessageKey(recipientId)
                        appVoiceRecorder.startRecord(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        chat_input_message_edit_text.setText("")
                        chat_voice_button.colorFilter = null
                        appVoiceRecorder.stopRecord { file, messageKey ->
                            //Получаем общее время записи
                            val recordingTime =
                                System.currentTimeMillis() - v.tag.toString().toLong()

                            if (recordingTime >= minTime) {
                                val uri = Uri.fromFile(file)
                                uploadFileToStorage(
                                    uri,
                                    messageKey
                                ) {
                                    sendMessageAsFile(
                                        recipientId,
                                        it,
                                        messageKey,
                                        MESSAGE_TYPE_VOICE,
                                        getFileNameFromUri(uri)
                                    )
                                    saveChatInMainList(recipientId, TYPE_CHAT)
                                }
                                isSmoothScrollToPosition = true
                            } else {
                                file.delete()
                            }
                        }
                    }
                }

                true
            }
        }
    }

    private fun attach() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottom_sheet_attach_file_button.setOnClickListener { attachFile() }
        bottom_sheet_attach_image_button.setOnClickListener { attachImage() }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data)?.uriContent
                    if (uri != null) {
                        val messageKey = getMessageKey(recipientId)
                        uploadFileToStorage(uri, messageKey) {
                            sendMessageAsFile(
                                recipientId,
                                it,
                                messageKey,
                                MESSAGE_TYPE_IMAGE,
                                getFileNameFromUri(uri)
                            )
                            saveChatInMainList(recipientId, TYPE_CHAT)
                        }
                        isSmoothScrollToPosition = true
                    }
                }

                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    if (uri != null) {
                        val messageKey = getMessageKey(recipientId)
                        uploadFileToStorage(
                            uri,
                            messageKey
                        ) {
                            sendMessageAsFile(
                                recipientId,
                                it,
                                messageKey,
                                MESSAGE_TYPE_FILE,
                                getFileNameFromUri(uri)
                            )
                            saveChatInMainList(recipientId, TYPE_CHAT)
                        }
                        isSmoothScrollToPosition = true
                    }
                }
            }
        }
    }

    private fun initToolbar() {
        toolbarInfo = APP_ACTIVITY.toolbar.chat_toolbar
        toolbarInfo.visibility = View.VISIBLE
        listenerInfoToolbar = AppValueEventListener {
            val userData = it.getCommonModel()
            userName = userData.fullname
            userPhotoUrl = userData.photoUrl
            initInfoToolbar()
        }

        refUser = REF_DATABASE_ROOT.child(DATABASE_NODE_USERS).child(recipientId)
        refUser.addValueEventListener(listenerInfoToolbar)

        chat_send_message_button.setOnClickListener {
            isSmoothScrollToPosition = true
            val messageText = chat_input_message_edit_text.text.toString()
            if (messageText.isEmpty()) {
                showToast(getString(R.string.chat_message_empty_input))
            } else {
                chat_input_message_edit_text.setText("")
                sendMessage(messageText, recipientId) {
                    saveChatInMainList(recipientId, TYPE_CHAT)
                }
            }
        }
    }

    private fun initRecyclerView() {
        recyclerView = chat_recycler_view
        adapter = ChatAdapter()
        refMessages = REF_DATABASE_ROOT
            .child(DATABASE_NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(recipientId)

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = layoutManager

        messagesListener = AppChildEventListener {
            val message = it.getCommonModel()

            addItem(message)
        }

        refMessages.limitToLast(totalCountLoadedMessages).addChildEventListener(messagesListener)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //Подгрузка сообщений, если первый видимый элемент в списке (самый верхний) 4 по
                //счету от общего количества подгруженных элементов (начиная с 0). Т.е. подгрузка
                //включается, если осталось отобразить 4 элемента.
                if (isScrolling && dy < 0 && layoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun addItem(message: CommonModel) {
        if (isSmoothScrollToPosition) {
            adapter.addItemToBottom(AppViewFactory.getView(message)) {
                recyclerView.smoothScrollToPosition(adapter.itemCount)
            }
        } else {
            adapter.addItemToTop(AppViewFactory.getView(message)) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun updateData() {
        isSmoothScrollToPosition = false
        isScrolling = false
        totalCountLoadedMessages += countUploadedMessages
        refMessages.removeEventListener(messagesListener)
        refMessages.limitToLast(totalCountLoadedMessages).addChildEventListener(messagesListener)
    }

    override fun onPause() {
        super.onPause()
        toolbarInfo.visibility = View.GONE
        refUser.removeEventListener(listenerInfoToolbar)
        refMessages.removeEventListener(messagesListener)
    }

    private fun initInfoToolbar() {
        toolbarInfo.chat_toolbar_fullname_text_view.text = userName
        toolbarInfo.chat_toolbar_user_photo_circle_image_view.downloadAndSetImage(userPhotoUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appVoiceRecorder.releaseRecorder()
        adapter.onDestroy()
        recipientId = ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        APP_ACTIVITY.menuInflater.inflate(R.menu.chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_chat -> {
                clearChat(recipientId) {
                    showToast(getString(R.string.chat_toast_chat_cleared))
                    adapter.clearList()
                }
                true
            }
            R.id.delete_chat -> {
                deleteChat(recipientId) {
                    showToast(getString(R.string.chat_toast_chat_deleted))
                    restartActivity()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        var recipientId: String = ""
    }
}