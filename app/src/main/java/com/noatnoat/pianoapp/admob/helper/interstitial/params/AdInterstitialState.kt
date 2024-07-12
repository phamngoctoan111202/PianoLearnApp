package com.noatnoat.pianoapp.admob.helper.interstitial.params

import com.google.android.gms.ads.interstitial.InterstitialAd

/**
 * Created by ViO on 16/03/2024.
 */
sealed class AdInterstitialState {
    data object None : AdInterstitialState()
    data object Fail : AdInterstitialState()
    data object Loading : AdInterstitialState()
    data object Loaded: AdInterstitialState()
    data object ShowFail: AdInterstitialState()
    data object Showed: AdInterstitialState()
    data object Cancel : AdInterstitialState()
    data class Show(val interstitialAd: InterstitialAd) : AdInterstitialState()
}