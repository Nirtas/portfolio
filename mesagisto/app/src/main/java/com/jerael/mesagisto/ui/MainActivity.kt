package com.jerael.mesagisto.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jerael.mesagisto.R
import com.jerael.mesagisto.database.*
import com.jerael.mesagisto.databinding.ActivityMainBinding
import com.jerael.mesagisto.utils.APP_ACTIVITY
import com.jerael.mesagisto.utils.AppThemeUtil


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP_ACTIVITY = this
        initFields()
        initFunc()

        if (AppThemeUtil.isDarkThemeEnabled(APP_ACTIVITY)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.data_container) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
    }

    private fun initFields() {
        toolbar = binding.mainToolbar
        setSupportActionBar(toolbar)
    }

    private fun initFunc() {
        initFirebase() {
            if (AUTH.currentUser != null) {
                initUser {
                    if (USER.fullname.isEmpty()) {
                        signOut()
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_main_list, true)
                            .build()

                        APP_ACTIVITY.supportFragmentManager.fragments[0].findNavController()
                            .navigate(R.id.navigation_phone_enter, null, navOptions)
                    }
                }
            } else {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_main_list, true)
                    .build()

                APP_ACTIVITY.supportFragmentManager.fragments[0].findNavController()
                    .navigate(R.id.navigation_phone_enter, null, navOptions)
            }
        }
    }
}