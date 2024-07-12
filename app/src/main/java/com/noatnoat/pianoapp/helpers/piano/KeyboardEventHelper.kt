package com.noatnoat.pianoapp.helpers.piano

import android.widget.Button

class KeyboardEventHelper(private val keys: Array<Button>) {

    fun disableKeyboard() {
        keys.forEach { it.isEnabled = false }
    }

    fun enableKeyboard() {
        keys.forEach { it.isEnabled = true }
    }
}