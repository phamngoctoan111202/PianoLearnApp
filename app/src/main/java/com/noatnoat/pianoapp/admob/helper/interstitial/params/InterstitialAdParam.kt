package com.noatnoat.pianoapp.admob.helper.interstitial.params

import com.google.android.gms.ads.interstitial.InterstitialAd
import com.noatnoat.pianoapp.admob.helper.params.IAdsParam

/**
 * Created by ViO on 16/03/2024.
 */
sealed class InterstitialAdParam: IAdsParam {
    data class Show(val interstitialAd: InterstitialAd) : InterstitialAdParam()
    data object ShowAd : InterstitialAdParam()
    data object Request : InterstitialAdParam() {
        @JvmStatic
        fun create(): Request {
            return this
        }
    }

    data class Clickable(
        val minimumTimeKeepAdsDisplay: Long
    ) : InterstitialAdParam()
}