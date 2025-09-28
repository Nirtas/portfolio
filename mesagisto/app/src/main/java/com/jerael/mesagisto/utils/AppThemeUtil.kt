package com.jerael.mesagisto.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class AppThemeUtil {
    companion object {
        private const val DARK_THEME = "DARK_THEME"

        fun isDarkThemeEnabled(context: Context): Boolean {
            val mPrefs: SharedPreferences = context.getSharedPreferences("APP_THEME", MODE_PRIVATE)
            return mPrefs.getBoolean(DARK_THEME, false)
        }

        fun setIsDarkThemeEnabled(context: Context, isDarkThemeEnabled: Boolean) {
            val mPrefs: SharedPreferences = context.getSharedPreferences("APP_THEME", MODE_PRIVATE)
            val editor = mPrefs.edit()
            editor.putBoolean(DARK_THEME, isDarkThemeEnabled)
            editor.apply()
        }
    }
}