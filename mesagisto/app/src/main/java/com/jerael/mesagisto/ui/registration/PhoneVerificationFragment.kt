package com.jerael.mesagisto.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.PhoneAuthProvider
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.checkSignInWithCredential
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.hideKeyboard
import com.jerael.mesagisto.utils.restartActivity
import com.jkb.vcedittext.VerificationAction
import kotlinx.android.synthetic.main.fragment_phone_verification.*

class PhoneVerificationFragment : Fragment() {

    private lateinit var phoneNumber: String
    private lateinit var id: String

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
        return inflater.inflate(R.layout.fragment_phone_verification, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phoneNumber = arguments?.getString("phoneNumber") ?: ""
        id = arguments?.getString("id") ?: ""
    }

    override fun onResume() {
        super.onResume()

        APP_ACTIVITY.title = phoneNumber
        phone = phoneNumber

        registration_phone_code_verification_code_edit_text.setOnVerificationCodeChangedListener(object :
            VerificationAction.OnVerificationCodeChangedListener {
            override fun onVerCodeChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onInputCompleted(s: CharSequence?) {
                verifyCode(s.toString())
            }
        })
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(id, code)
        checkSignInWithCredential(credential) {
            when (it) {
                true -> {
                    val bundle = Bundle()
                    bundle.putBoolean("isRegistration", true)
                    findNavController().navigate(R.id.navigation_change_fullname, bundle)
                }
                false -> restartActivity()
            }
        }
    }

    companion object {
        var phone: String = ""
    }
}