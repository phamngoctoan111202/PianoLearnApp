package com.noatnoat.pianoapp.admob.helper.adnative

import androidx.annotation.LayoutRes
import com.noatnoat.pianoapp.admob.helper.IAdsConfig

/**
 * Created by ViO on 16/03/2024.
 */
class NativeAdConfig(
    override val idAds: String,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean,
    @LayoutRes val layoutId: Int,
) : IAdsConfig
