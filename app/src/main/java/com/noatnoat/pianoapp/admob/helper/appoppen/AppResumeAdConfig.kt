package com.noatnoat.pianoapp.admob.helper.appoppen

import com.noatnoat.pianoapp.admob.helper.IAdsConfig

/**
 * Created by ViO on 16/03/2024.
 */
class AppResumeAdConfig(
    override val idAds: String,
    val listClassInValid: MutableList<Class<*>> = arrayListOf(),
    override val canShowAds: Boolean = false,
    override val canReloadAds: Boolean = false
) : IAdsConfig