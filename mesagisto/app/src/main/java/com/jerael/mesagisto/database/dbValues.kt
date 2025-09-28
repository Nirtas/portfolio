package com.jerael.mesagisto.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.jerael.mesagisto.models.CommonModel

lateinit var AUTH: FirebaseAuth
lateinit var USER: CommonModel
lateinit var CURRENT_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
const val DATABASE_NODE_USERS = "users"
const val DATABASE_NODE_LOGINS = "logins"
const val DATABASE_NODE_PHONES = "phones"
const val DATABASE_NODE_PHONE_CONTACTS = "phone_contacts"
const val DATABASE_NODE_MESSAGES = "messages"
const val DATABASE_NODE_MAIN_LIST = "main_list"
const val DATABASE_NODE_GROUPS = "groups"
const val DATABASE_NODE_MEMBERS = "members"
const val DATABASE_CHILD_ID = "id"
const val DATABASE_CHILD_LOGIN = "login"
const val DATABASE_CHILD_PHONE = "phone"
const val DATABASE_CHILD_FULLNAME = "fullname"
const val DATABASE_CHILD_PHOTO_URL = "photoUrl"
const val DATABASE_CHILD_USER_INFO = "userInfo"
const val DATABASE_CHILD_MESSAGE_TEXT = "text"
const val DATABASE_CHILD_TYPE = "type"
const val DATABASE_CHILD_MESSAGE_SENDER = "sender"
const val DATABASE_CHILD_MESSAGE_TIMESTAMP = "timestamp"
const val DATABASE_CHILD_MESSAGE_FILE_URL = "fileUrl"
const val DATABASE_CHILD_MESSAGE_FILE_NAME = "fileName"
const val STORAGE_FOLDER_PROFILE_IMAGE = "profile_image"
const val STORAGE_FOLDER_MESSAGES_FILES = "messages_files"
const val FOLDER_GROUPS_IMAGE = "groups_image"
const val GROUP_USER_MEMBER = "member"