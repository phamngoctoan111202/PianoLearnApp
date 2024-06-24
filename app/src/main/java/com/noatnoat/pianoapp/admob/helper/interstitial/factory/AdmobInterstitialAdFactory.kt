package com.noatnoat.pianoapp.admob.helper.interstitial.factory

import android.content.Context
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.noatnoat.pianoapp.admob.listener.InterstitialAdCallback
/**
 * Created by ViO on 16/03/2024.
 */
interface AdmobInterstitialAdFactory {
    fun requestInterstitialAd(context: Context, adId: String, adCallback: InterstitialAdCallback)
    fun showInterstitial(
        context: Context,
        interstitialAd: InterstitialAd?,
        adCallback: InterstitialAdCallback
    )

    companion object {
        @JvmStatic
        fun getInstance(): AdmobInterstitialAdFactory = AdmobInterstitialAdFactoryImpl()
    }
}