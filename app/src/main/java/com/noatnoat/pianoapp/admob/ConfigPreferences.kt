package com.noatnoat.pianoapp.admob

import android.annotation.SuppressLint
import android.content.Context
import com.noatnoat.pianoapp.utils.KeyRemoteConfigDefault.*

class ConfigPreferences private constructor(context: Context) : Preferences(context, "BloodPressure") {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var instance: ConfigPreferences? = null // Volatile modifier is necessary

        fun getInstance(context: Context) =
            instance ?: synchronized(this) { // synchronized to avoid concurrency problem
                instance ?: ConfigPreferences(context).also { instance = it }
            }
    }
    var isEnableUMP by booleanPref(ENABLE_UMP, true)
    var isShowNativeLanguage by booleanPref(CONFIG_NATIVE_LANGUAGE, true)
    var isShowNativeOnboarding by booleanPref(CONFIG_NATIVE_ONBOARDING, true)
    var isShowNativePermission by booleanPref(CONFIG_NATIVE_PERMISSION, true)
    var isShowNativeExit by booleanPref(CONFIG_NATIVE_EXIT, true)
    var isShowNativeHome by booleanPref(CONFIG_NATIVE_HOME, true)

    var isShowBannerCollapsibleHome by booleanPref(CONFIG_BANNER_COLLAP_HOME, true)
    var isShowBanner by booleanPref(CONFIG_BANNER, true)
    var isShowBannerSplash by booleanPref(CONFIG_BANNER_SPLASH, true)

    var isShowInterSplash by booleanPref(CONFIG_INTER_SPLASH, true)
    var isShowInterHome by booleanPref(CONFIG_INTER_HOME, true)

    var isShowLfo1HighFloor by booleanPref(LFO_1_HIGH_FLOOR, true)
    var isShowLfo2HighFloor by booleanPref(LFO_2_HIGH_FLOOR, true)

    var isShowAppOpen by booleanPref(CONFIG_APPOPEN_RESUME, true)

}

