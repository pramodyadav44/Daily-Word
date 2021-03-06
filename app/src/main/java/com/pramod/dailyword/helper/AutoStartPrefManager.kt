package com.pramod.dailyword.helper

import android.content.Context

class AutoStartPrefManager private constructor(private val context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()


    companion object {
        private const val PREFERENCES_NAME = "auto_start_pref"
        private const val KEY_CLICK_ON_ALREADY_ENABLED = "click_on_already_enabled"
        private const val KEY_CLICK_ON_SETTING = "click_on_setting"
        fun newInstance(context: Context) = AutoStartPrefManager(context)
    }

    fun clickedOnAlreadyEnabled() {
        editor.putBoolean(KEY_CLICK_ON_ALREADY_ENABLED, true).commit()
    }

    fun clickedOnSetting() {
        editor.putBoolean(KEY_CLICK_ON_SETTING, true).commit()
    }

    fun isAutoStartAlreadyEnabled() =
        sharedPreferences.getBoolean(KEY_CLICK_ON_ALREADY_ENABLED, false)

    fun isClickedOnSetting() = sharedPreferences.getBoolean(KEY_CLICK_ON_SETTING, false)

}