package com.noatnoat.pianoapp.admob.helper.adnative.factory

import android.content.Context
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAd
import com.noatnoat.pianoapp.admob.listener.NativeAdCallback

/**
 * Created by ViO on 16/03/2024.
 */
interface AdmobNativeFactory {
    fun requestNativeAd(context: Context, adId: String, adCallback: NativeAdCallback)

    fun populateNativeAdView(
        activity: Context,
        nativeAd: NativeAd,
        @LayoutRes nativeAdViewId: Int,
        adPlaceHolder: FrameLayout,
        containerShimmerLoading: ShimmerFrameLayout?,
        adCallback: NativeAdCallback
    )

    companion object {
        @JvmStatic
        fun getInstance(): AdmobNativeFactory = AdmobNativeFactoryImpl()
    }
}