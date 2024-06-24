package com.noatnoat.pianoapp.admob.helper.banner

import com.noatnoat.pianoapp.admob.helper.IAdsConfig


/**
 * Created by ViO on 16/03/2024.
 */
data class BannerAdConfig(
    override val idAds: String,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean,
) : IAdsConfig {
    var collapsibleGravity: String? = null
}