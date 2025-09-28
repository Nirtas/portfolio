package com.jerael.mesagisto.models

data class CommonModel(
    val id: String = "",
    var login: String = "",
    var phone: String = "",
    var fullname: String = "",
    var photoUrl: String = "empty",
    var userInfo: String = "",

    var text: String = "",
    var type: String = "",
    var sender: String = "",
    var timestamp: Any = "",
    var fileUrl: String = "empty",
    var fileName: String = "",

    var lastMessage: String = "",
    var check: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}