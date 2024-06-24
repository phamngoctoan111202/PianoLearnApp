package com.noatnoat.pianoapp.admob.helper.banner.params

import com.google.android.gms.ads.AdView

/**
 * Created by ViO on 16/03/2024.
 */
sealed class AdBannerState {
    object None : AdBannerState()
    object Fail : AdBannerState()
    object Loading : AdBannerState()
    object Cancel : AdBannerState()
    data class Loaded(val adBanner: AdView) : AdBannerState()
}