package com.noatnoat.pianoapp.admob

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
/**
 * Created by ViO on 16/03/2024.
 */

private const val MAX_SMALL_INLINE_BANNER_HEIGHT = 50
fun getAdRequest(): AdRequest {
    val builder = AdRequest.Builder()
    return builder.build()
}

fun getCollapsibleAdRequest(type: String): AdRequest {
    val builder = AdRequest.Builder()
    builder.addNetworkExtrasBundle(AdMobAdapter::class.java, Bundle().apply {
        putString("collapsible", type)
    })
    return builder.build()
}

fun getAdSize(
    mActivity: Activity,
    useInlineAdaptive: Boolean,
    inlineStyle: Int
): AdSize {

    // Step 2 - Determine the screen width (less decorations) to use for the ad width.
    val display = mActivity.windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)
    val widthPixels = outMetrics.widthPixels.toFloat()
    val density = outMetrics.density
    val adWidth = (widthPixels / density).toInt()

    // Step 3 - Get adaptive ad size and return for setting on the ad view.
    return if (useInlineAdaptive) {
        if (inlineStyle == BannerInlineStyle.LARGE_STYLE) {
            AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(
                mActivity,
                adWidth
            )
        } else {
            AdSize.getInlineAdaptiveBannerAdSize(
                adWidth,
                MAX_SMALL_INLINE_BANNER_HEIGHT
            )
        }
    } else AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
        mActivity,
        adWidth
    )
}

@IntDef(BannerInlineStyle.SMALL_STYLE, BannerInlineStyle.LARGE_STYLE)
annotation class BannerInlineStyle {
    companion object {
        const val SMALL_STYLE = 0
        const val LARGE_STYLE = 1
    }
}

@StringDef(BannerCollapsibleGravity.BOTTOM, BannerCollapsibleGravity.TOP)
annotation class BannerCollapsibleGravity {
    companion object {
        const val BOTTOM = "bottom"
        const val TOP = "top"
    }
}