package com.noatnoat.pianoapp.utils

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsUtil {

    companion object {
        const val SPLASH_OPEN = "splash_open"
        const val LANGUAGE_FO_OPEN = "language_fo_open"
        const val LANGUAGE_FO_SAVE_CLICK = "language_fo_save_click"
        const val ONBOARDING1_NEXT_VIEW = "onboarding1_next_view"
        const val ONBOARDING1_NEXT_CLICK = "onboarding1_next_click"
        const val ONBOARDING2_NEXT_VIEW = "onboarding2_next_view"
        const val ONBOARDING2_NEXT_CLICK = "onboarding2_next_click"
        const val ONBOARDING3_NEXT_VIEW = "onboarding3_next_view"
        const val ONBOARDING3_NEXT_CLICK = "onboarding3_next_click"
        const val SETTING_VIEW = "setting_view"
        const val HOME_VIEW = "home_view"
        const val HOME_CREATE_CLICK = "home_create_click"


        fun logEvent(event: String, bundle: Bundle? = null) {
            Firebase.analytics.logEvent(event, bundle)
        }
    }

}