package com.noatnoat.pianoapp.admob.listener

import com.noatnoat.pianoapp.admob.data.ContentAd

/**
 * Created by ViO on 16/03/2024.
 */
interface InterstitialAdCallback : ViOAdCallback<ContentAd.AdmobAd.ApInterstitialAd> {
    fun onNextAction()
    fun onAdClose()
    fun onInterstitialShow()
}