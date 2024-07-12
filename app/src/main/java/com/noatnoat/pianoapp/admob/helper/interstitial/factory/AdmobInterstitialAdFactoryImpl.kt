package com.noatnoat.pianoapp.admob.helper.interstitial.factory

import android.app.Activity
import android.content.Context
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.noatnoat.pianoapp.admob.AdmobManager
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.getAdRequest
import com.noatnoat.pianoapp.admob.listener.InterstitialAdCallback

/**
 * Created by ViO on 16/03/2024.
 */
class AdmobInterstitialAdFactoryImpl : AdmobInterstitialAdFactory {
    override fun requestInterstitialAd(
        context: Context,
        adId: String,
        adCallback: InterstitialAdCallback
    ) {
        InterstitialAd.load(
            context,
            adId,
            getAdRequest(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adCallback.onAdFailedToLoad(adError)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    adCallback.onAdLoaded(ContentAd.AdmobAd.ApInterstitialAd(ad))
                    try{
                        ad.setOnPaidEventListener {
                            val loadedAdapterResponseInfo: AdapterResponseInfo? = ad.responseInfo?.loadedAdapterResponseInfo
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            adRevenue.setRevenue(it.valueMicros / 1000000.0, it.currencyCode)
                            adRevenue.setAdRevenuePlacement("Interstitial")
                            if (loadedAdapterResponseInfo != null) {
                                adRevenue.setAdRevenueNetwork(loadedAdapterResponseInfo.adSourceName)
                            }
                            Adjust.trackAdRevenue(adRevenue)
                        }
                    }catch (e:Exception){}
                }
            }
        )
    }

    override fun showInterstitial(
        context: Context,
        interstitialAd: InterstitialAd?,
        adCallback: InterstitialAdCallback
    ) {
        if (interstitialAd == null) {
            adCallback.onNextAction()
            return
        }

        interstitialAd.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    adCallback.onAdClose()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    adCallback.onAdFailedToShow(adError)
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is dismissed.
                    adCallback.onInterstitialShow()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    AdmobManager.adsClicked()
                    adCallback.onAdClicked()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    adCallback.onAdImpression()
                }
            }
        interstitialAd.show(context as Activity)
    }
}