package com.jerael.mesagisto.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.AUTH
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.hideKeyboard
import com.jerael.mesagisto.utils.showToast
import kotlinx.android.synthetic.main.fragment_phone_enter.*
import java.util.concurrent.TimeUnit

class PhoneEnterFragment : Fragment() {

    private lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        APP_ACTIVITY.binding.navView.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_phone_enter, container, false)
    }

    override fun onResume() {
        super.onResume()

        APP_ACTIVITY.title = getString(R.string.registration_title_phone_enter)

        registration_next_floating_action_button.setOnClickListener {
            sendCode()
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun sendCode() {
        if (registration_phone_number_edit_text.text.toString().isEmpty()) {
            showToast(getString(R.string.registration_toast_empty_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        phoneNumber = registration_phone_number_edit_text.text.toString()

        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(phoneNumber)
            .setTimeout(3, TimeUnit.SECONDS)
            .setActivity(APP_ACTIVITY)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

                override fun onVerificationFailed(p0: FirebaseException) {
                    showToast(p0.message.toString())
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    val bundle = Bundle()
                    bundle.putString("phoneNumber", phoneNumber)
                    bundle.putString("id", id)
                    findNavController().navigate(R.id.navigation_phone_verification, bundle)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}