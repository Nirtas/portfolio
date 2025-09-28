package com.jerael.mesagisto.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.text.InputType
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.updatePhonesInDb
import com.jerael.mesagisto.models.CommonModel
import com.jerael.mesagisto.ui.MainActivity
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun Fragment.hideKeyboard() {
    val inputMethodManager: InputMethodManager =
        this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(APP_ACTIVITY.window?.decorView?.windowToken, 0)
}

fun ImageView.downloadAndSetImage(photoUrl: String) {
    Picasso
        .get()
        .load(photoUrl)
        .fit()
        .placeholder(R.drawable.ic_default_user_photo)
        .into(this)
}

fun initContacts() {
    if (checkPermission(READ_CONTACTS)) {
        val arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullname =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullname
                newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhonesInDb(arrayContacts)
    }
}

fun String.toTimeFormat(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun getFileNameFromUri(uri: Uri): String {
    var result = ""
    val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null, null)
    try {
        if (cursor != null && cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    } catch (e: Exception) {
        showToast(e.message.toString())
    }

    cursor?.close()
    return result
}

fun showDeleteMessageDialog(function: () -> Unit) {
    val builder = AlertDialog.Builder(APP_ACTIVITY)
    builder.setTitle(APP_ACTIVITY.getString(R.string.alert_dialog_delete_message_title))
        .setMessage(APP_ACTIVITY.getString(R.string.alert_dialog_delete_message_content))
        .setPositiveButton(APP_ACTIVITY.getString(R.string.alert_dialog_button_delete)) { dialogInterface: DialogInterface, i: Int ->
            function()
        }
        .setNegativeButton(APP_ACTIVITY.getString(R.string.alert_dialog_button_cancel)) { dialogInterface: DialogInterface, i: Int -> }

    val dialog = builder.create()
    dialog.show()

    val typedValue = TypedValue()
    APP_ACTIVITY.theme.resolveAttribute(R.attr.colorPositiveButton, typedValue, true)
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(typedValue.data)
    APP_ACTIVITY.theme.resolveAttribute(R.attr.colorNegativeButton, typedValue, true)
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(typedValue.data)
}

fun showEditTextMessageDialog(currentText: String, function: (newText: String) -> Unit) {
    val builder = AlertDialog.Builder(APP_ACTIVITY)
    builder.setTitle(APP_ACTIVITY.getString(R.string.alert_dialog_edit_message_title))

    val editText = EditText(APP_ACTIVITY)
    editText.inputType = InputType.TYPE_CLASS_TEXT
    editText.setText(currentText)
    editText.setSelection(editText.text.toString().length)
    builder.setView(editText)

    builder
        .setPositiveButton(APP_ACTIVITY.getString(R.string.alert_dialog_button_save)) { dialogInterface: DialogInterface, i: Int ->
            val newText = editText.text.toString()
            if (newText.isNotEmpty()) {
                function(newText)
            } else {
                showToast(APP_ACTIVITY.getString(R.string.chat_message_empty_input))
            }
        }
        .setNegativeButton(APP_ACTIVITY.getString(R.string.alert_dialog_button_cancel)) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
        }

    val dialog = builder.create()
    dialog.show()

    val typedValue = TypedValue()
    APP_ACTIVITY.theme.resolveAttribute(R.attr.colorPositiveButton, typedValue, true)
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(typedValue.data)
    APP_ACTIVITY.theme.resolveAttribute(R.attr.colorNegativeButton, typedValue, true)
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(typedValue.data)
}