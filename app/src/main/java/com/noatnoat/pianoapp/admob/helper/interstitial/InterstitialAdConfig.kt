package com.noatnoat.pianoapp.admob.helper.interstitial

import com.noatnoat.pianoapp.admob.helper.IAdsConfig
/**
 * Created by ViO on 16/03/2024.
 */
class InterstitialAdConfig(
    override val idAds: String,
    val showByTime: Int = 1,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean
) : IAdsConfig