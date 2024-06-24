package com.noatnoat.pianoapp.admob.helper.interstitial


import com.noatnoat.pianoapp.admob.helper.IAdsConfig
/**
 * Created by ViO on 16/03/2024.
 */
class InterstitialAdSplashConfig(
    override val idAds: String,
    val timeOut: Long,
    val timeDelay: Long,
    val showReady: Boolean = false,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean
) : IAdsConfig