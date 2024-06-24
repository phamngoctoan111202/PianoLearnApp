package com.noatnoat.pianoapp.admob.helper.appoppen.params

import com.noatnoat.pianoapp.admob.helper.params.IAdsParam

/**
 * Created by ViO on 16/03/2024.
 */
open class AppOpenAdParam : IAdsParam {
    data object Show : AppOpenAdParam()
    data object Request : AppOpenAdParam() {
        @JvmStatic
        fun create(): Request {
            return this
        }
    }

    data class Clickable(
        val minimumTimeKeepAdsDisplay: Long
    ) : AppOpenAdParam()
}