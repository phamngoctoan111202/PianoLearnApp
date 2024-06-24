package com.noatnoat.pianoapp.ui.base

import androidx.preference.PreferenceFragmentCompat

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    abstract val titleResourceId: Int

}