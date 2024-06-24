package com.noatnoat.pianoapp.admob.helper.banner.params

import com.google.android.gms.ads.AdView
import com.noatnoat.pianoapp.admob.helper.params.IAdsParam

/**
 * Created by ViO on 16/03/2024.
 */
sealed class BannerAdParam: IAdsParam {
    data class Ready(val bannerAds: AdView) : BannerAdParam()
    object Request : BannerAdParam() {
        @JvmStatic
        fun create(): Request {
            return this
        }
    }

    data class Clickable(
        val minimumTimeKeepAdsDisplay: Long
    ) : BannerAdParam()
}