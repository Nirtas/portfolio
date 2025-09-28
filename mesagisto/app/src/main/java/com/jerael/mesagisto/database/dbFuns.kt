package com.jerael.mesagisto.database

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.utils.AppValueEventListener
import com.jerael.mesagisto.utils.MESSAGE_TYPE_TEXT
import com.jerael.mesagisto.utils.TYPE_GROUP
import com.jerael.mesagisto.utils.showToast
import java.io.File
import java.util.*

inline fun initFirebase(crossinline function: () -> Unit) {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = CommonModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
    function()
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_USERS)
        .child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getCommonModel()
            if (USER.login.isEmpty()) {
                USER.login = CURRENT_UID
            }
            function()
        })
}

fun signOut() {
    AUTH.signOut()
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putUrlToDb(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_USERS)
        .child(CURRENT_UID)
        .child(DATABASE_CHILD_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun updatePhonesInDb(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT
            .child(DATABASE_NODE_PHONES)
            .addListenerForSingleValueEvent(AppValueEventListener {
                it.children.forEach { dataSnapshot ->
                    arrayContacts.forEach { contact ->
                        if (dataSnapshot.key == contact.phone && contact.phone != USER.phone) {
                            REF_DATABASE_ROOT
                                .child(DATABASE_NODE_PHONE_CONTACTS)
                                .child(CURRENT_UID)
                                .child(dataSnapshot.value.toString())
                                .child(DATABASE_CHILD_ID)
                                .setValue(dataSnapshot.value.toString())
                                .addOnFailureListener { fail -> showToast(fail.message.toString()) }

                            REF_DATABASE_ROOT
                                .child(DATABASE_NODE_PHONE_CONTACTS)
                                .child(CURRENT_UID)
                                .child(dataSnapshot.value.toString())
                                .child(DATABASE_CHILD_FULLNAME)
                                .setValue(contact.fullname)
                                .addOnFailureListener { fail -> showToast(fail.message.toString()) }
                        }
                    }
                }
            })
    }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun sendMessage(
    messageText: String,
    recipientId: String,
    function: () -> Unit
) {
    val refDialogSenderUser = "$DATABASE_NODE_MESSAGES/$CURRENT_UID/$recipientId"
    val refDialogRecipientUser = "$DATABASE_NODE_MESSAGES/$recipientId/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogSenderUser).push().key

    val messageMap = hashMapOf<String, Any>()
    messageMap[DATABASE_CHILD_MESSAGE_SENDER] = CURRENT_UID
    messageMap[DATABASE_CHILD_TYPE] = MESSAGE_TYPE_TEXT
    messageMap[DATABASE_CHILD_MESSAGE_TEXT] = messageText
    messageMap[DATABASE_CHILD_ID] = messageKey.toString()
    messageMap[DATABASE_CHILD_MESSAGE_TIMESTAMP] = ServerValue.TIMESTAMP

    val dialogMap = hashMapOf<String, Any>()
    dialogMap["$refDialogSenderUser/$messageKey"] = messageMap
    dialogMap["$refDialogRecipientUser/$messageKey"] = messageMap

    REF_DATABASE_ROOT
        .updateChildren(dialogMap)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun saveFullnameInDb(fullname: String, function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_USERS)
        .child(CURRENT_UID)
        .child(DATABASE_CHILD_FULLNAME)
        .setValue(fullname)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun searchLoginInDb(newLogin: String, function: (isUsed: Boolean) -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_LOGINS)
        .addListenerForSingleValueEvent(AppValueEventListener {
            if (it.hasChild(newLogin)) {
                function(true)
            } else {
                function(false)
            }
        })
}

fun changeLogin(newLogin: String, function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_LOGINS)
        .child(newLogin)
        .setValue(CURRENT_UID)
        .addOnSuccessListener {
            updateCurrentLogin(newLogin) {
                function()
            }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun updateCurrentLogin(newLogin: String, function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_USERS)
        .child(CURRENT_UID)
        .child(DATABASE_CHILD_LOGIN)
        .setValue(newLogin)
        .addOnSuccessListener {
            deleteOldLogin {
                function()
            }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteOldLogin(function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_LOGINS)
        .child(USER.login)
        .removeValue()
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun saveUserInfoInDb(newUserInfo: String, function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_USERS)
        .child(CURRENT_UID)
        .child(DATABASE_CHILD_USER_INFO)
        .setValue(newUserInfo)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsFile(
    recipientId: String,
    fileUrl: String,
    messageKey: String,
    messageType: String,
    fileName: String
) {
    val refDialogSenderUser = "$DATABASE_NODE_MESSAGES/$CURRENT_UID/$recipientId"
    val refDialogRecipientUser = "$DATABASE_NODE_MESSAGES/$recipientId/$CURRENT_UID"

    val messageMap = hashMapOf<String, Any>()
    messageMap[DATABASE_CHILD_MESSAGE_SENDER] = CURRENT_UID
    messageMap[DATABASE_CHILD_TYPE] = messageType
    messageMap[DATABASE_CHILD_MESSAGE_FILE_URL] = fileUrl
    messageMap[DATABASE_CHILD_ID] = messageKey
    messageMap[DATABASE_CHILD_MESSAGE_TIMESTAMP] = ServerValue.TIMESTAMP
    messageMap[DATABASE_CHILD_MESSAGE_FILE_NAME] = fileName

    val dialogMap = hashMapOf<String, Any>()
    dialogMap["$refDialogSenderUser/$messageKey"] = messageMap
    dialogMap["$refDialogRecipientUser/$messageKey"] = messageMap

    REF_DATABASE_ROOT
        .updateChildren(dialogMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMessageKey(id: String) = REF_DATABASE_ROOT.child(DATABASE_NODE_MESSAGES).child(CURRENT_UID)
    .child(id).push().key.toString()

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    function: (url: String) -> Unit
) {
    val path = REF_STORAGE_ROOT.child(STORAGE_FOLDER_MESSAGES_FILES).child(messageKey)

    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            function(it)
        }
    }
}

fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun saveChatInMainList(id: String, type: String) {
    val refSenderUser = "$DATABASE_NODE_MAIN_LIST/$CURRENT_UID/$id"
    val refRecipientUser = "$DATABASE_NODE_MAIN_LIST/$id/$CURRENT_UID"

    val senderUserMap = hashMapOf<String, Any>()
    senderUserMap[DATABASE_CHILD_ID] = id
    senderUserMap[DATABASE_CHILD_TYPE] = type

    val recipientUserMap = hashMapOf<String, Any>()
    recipientUserMap[DATABASE_CHILD_ID] = CURRENT_UID
    recipientUserMap[DATABASE_CHILD_TYPE] = type

    val dataMap = hashMapOf<String, Any>()
    dataMap[refSenderUser] = senderUserMap
    dataMap[refRecipientUser] = recipientUserMap

    REF_DATABASE_ROOT.updateChildren(dataMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_MAIN_LIST)
        .child(CURRENT_UID)
        .child(id)
        .removeValue()
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_MESSAGES)
        .child(CURRENT_UID)
        .child(id)
        .removeValue()
        .addOnSuccessListener {
            REF_DATABASE_ROOT
                .child(DATABASE_NODE_MESSAGES)
                .child(id)
                .child(CURRENT_UID)
                .removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun saveGroupInDb(
    groupName: String,
    uri: Uri,
    listUsers: List<CommonModel>,
    function: () -> Unit
) {
    val groupKey = REF_DATABASE_ROOT.child(DATABASE_NODE_GROUPS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(DATABASE_NODE_GROUPS).child(groupKey)
    val storagePath = REF_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(groupKey)

    putFileToStorage(uri, storagePath) {
        getUrlFromStorage(storagePath) { imageUrl ->
            val dataMap = hashMapOf<String, Any>()
            dataMap[DATABASE_CHILD_ID] = groupKey
            dataMap[DATABASE_CHILD_FULLNAME] = groupName
            dataMap[DATABASE_CHILD_PHOTO_URL] = imageUrl

            val membersMap = hashMapOf<String, Any>()
            listUsers.forEach {
                membersMap[it.id] = GROUP_USER_MEMBER
            }

            membersMap[CURRENT_UID] = GROUP_USER_MEMBER

            dataMap[DATABASE_NODE_MEMBERS] = membersMap

            path.updateChildren(dataMap)
                .addOnSuccessListener {
                    saveGroupInMainList(dataMap, listUsers) {
                        function()
                    }
                }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
    }
}

fun saveGroupInMainList(
    dataMap: HashMap<String, Any>,
    listUsers: List<CommonModel>,
    function: () -> Unit
) {
    val path = REF_DATABASE_ROOT.child(DATABASE_NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()

    map[DATABASE_CHILD_ID] = dataMap[DATABASE_CHILD_ID].toString()
    map[DATABASE_CHILD_TYPE] = TYPE_GROUP

    listUsers.forEach {
        path.child(it.id).child(map[DATABASE_CHILD_ID].toString()).updateChildren(map)
    }

    path.child(CURRENT_UID).child(map[DATABASE_CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageToGroup(
    messageText: String,
    groupId: String
) {
    val refMessages = "$DATABASE_NODE_GROUPS/$groupId/$DATABASE_NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key.toString()

    val messageMap = hashMapOf<String, Any>()
    messageMap[DATABASE_CHILD_MESSAGE_SENDER] = CURRENT_UID
    messageMap[DATABASE_CHILD_TYPE] = MESSAGE_TYPE_TEXT
    messageMap[DATABASE_CHILD_MESSAGE_TEXT] = messageText
    messageMap[DATABASE_CHILD_ID] = messageKey
    messageMap[DATABASE_CHILD_MESSAGE_TIMESTAMP] = ServerValue.TIMESTAMP

    REF_DATABASE_ROOT.child(refMessages).child(messageKey)
        .updateChildren(messageMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsFileToGroup(
    groupId: String,
    fileUrl: String,
    messageKey: String,
    messageType: String,
    fileName: String
) {
    val refMessages = "$DATABASE_NODE_GROUPS/$groupId/$DATABASE_NODE_MESSAGES"

    val messageMap = hashMapOf<String, Any>()
    messageMap[DATABASE_CHILD_MESSAGE_SENDER] = CURRENT_UID
    messageMap[DATABASE_CHILD_TYPE] = messageType
    messageMap[DATABASE_CHILD_MESSAGE_FILE_URL] = fileUrl
    messageMap[DATABASE_CHILD_ID] = messageKey
    messageMap[DATABASE_CHILD_MESSAGE_TIMESTAMP] = ServerValue.TIMESTAMP
    messageMap[DATABASE_CHILD_MESSAGE_FILE_NAME] = fileName

    REF_DATABASE_ROOT.child(refMessages).child(messageKey)
        .updateChildren(messageMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun exitGroup(userId: String, groupId: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(DATABASE_NODE_GROUPS).child(groupId).child(DATABASE_NODE_MEMBERS)
        .child(userId).removeValue()
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(DATABASE_NODE_MAIN_LIST).child(userId).child(groupId)
                .removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getUserFromDb(sender: String, function: (data: CommonModel) -> Unit) {
    REF_DATABASE_ROOT.child(DATABASE_NODE_USERS).child(sender)
        .addListenerForSingleValueEvent(AppValueEventListener {
            function(it.getCommonModel())
        })
}

fun deleteChatMessageInDb(messageId: String, recipientId: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(DATABASE_NODE_MESSAGES).child(CURRENT_UID).child(recipientId)
        .child(messageId).removeValue()
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(DATABASE_NODE_MESSAGES).child(recipientId).child(CURRENT_UID)
                .child(messageId).removeValue()
                .addOnSuccessListener {
                    function()
                }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteGroupChatMessageInDb(messageId: String, groupId: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(DATABASE_NODE_GROUPS).child(groupId).child(DATABASE_NODE_MESSAGES)
        .child(messageId).removeValue()
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun editChatMessageInDb(
    messageId: String,
    recipientId: String,
    messageText: String,
    function: () -> Unit
) {
    REF_DATABASE_ROOT.child(DATABASE_NODE_MESSAGES).child(CURRENT_UID).child(recipientId)
        .child(messageId).child(DATABASE_CHILD_MESSAGE_TEXT).setValue(messageText)
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(DATABASE_NODE_MESSAGES).child(recipientId).child(CURRENT_UID)
                .child(messageId).child(DATABASE_CHILD_MESSAGE_TEXT).setValue(messageText)
                .addOnSuccessListener {
                    function()
                }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun editGroupChatMessageInDb(
    messageId: String,
    groupId: String,
    messageText: String,
    function: () -> Unit
) {
    REF_DATABASE_ROOT.child(DATABASE_NODE_GROUPS).child(groupId).child(DATABASE_NODE_MESSAGES)
        .child(messageId).child(DATABASE_CHILD_MESSAGE_TEXT).setValue(messageText)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun saveUserInDb(phoneNumber: String, function: () -> Unit) {
    val uid = AUTH.currentUser?.uid.toString()
    val dataMap = mutableMapOf<String, Any>()
    dataMap[DATABASE_CHILD_ID] = uid
    dataMap[DATABASE_CHILD_PHONE] = phoneNumber
    dataMap[DATABASE_CHILD_LOGIN] = uid

    REF_DATABASE_ROOT
        .child(DATABASE_NODE_PHONES)
        .child(phoneNumber)
        .setValue(uid)
        .addOnSuccessListener {
            REF_DATABASE_ROOT
                .child(DATABASE_NODE_USERS)
                .child(uid)
                .updateChildren(dataMap)
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun checkUserInDb(function: (isNewUser: Boolean) -> Unit) {
    REF_DATABASE_ROOT
        .child(DATABASE_NODE_USERS)
        .addListenerForSingleValueEvent(AppValueEventListener {
            if (!it.hasChild(AUTH.currentUser?.uid.toString())) {
                function(true)
            } else {
                function(false)
            }
        })
}

fun checkSignInWithCredential(
    credential: AuthCredential,
    function: (isNewUser: Boolean) -> Unit
) {
    AUTH.signInWithCredential(credential)
        .addOnSuccessListener {
            checkUserInDb() { isNewUser ->
                function(isNewUser)
            }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}