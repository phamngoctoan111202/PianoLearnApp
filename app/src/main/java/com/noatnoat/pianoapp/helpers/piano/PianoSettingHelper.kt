package com.noatnoat.pianoapp.helpers.piano

import android.content.Context

object PianoSettingHelper {
    private const val PREFS_NAME = "PianoSettings"
    private const val SPEED_KEY = "Speed"

    fun setSpeed(context: Context, speed: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(SPEED_KEY, speed).apply()
    }

    fun getSpeed(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(SPEED_KEY, 50) // Default value is 50
    }
}