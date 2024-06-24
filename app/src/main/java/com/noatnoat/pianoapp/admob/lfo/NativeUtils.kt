package com.noatnoat.pianoapp.admob.lfo

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.admob.AdmobFactory
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.listener.NativeAdCallback

/**
 * Created by ViO on 17/03/2024.
 */
internal object NativeUtils {
    private val TAG = NativeUtils::class.simpleName
    var nativeLFO1: MutableLiveData<ContentAd.AdmobAd.ApNativeAd?> = MutableLiveData()
    var nativeLFO2: MutableLiveData<ContentAd.AdmobAd.ApNativeAd?> = MutableLiveData()
    var nativeOb1: MutableLiveData<ContentAd.AdmobAd.ApNativeAd?> = MutableLiveData()
    var nativeOb2: MutableLiveData<ContentAd.AdmobAd.ApNativeAd?> = MutableLiveData()
    var nativeOb3: MutableLiveData<ContentAd.AdmobAd.ApNativeAd?> = MutableLiveData()
    var nativeExit: MutableLiveData<ContentAd.AdmobAd.ApNativeAd?> = MutableLiveData()
    private var requestNativeExitValid = true
    fun requestNativeExit(
        context: Context,
        idAd: String,
        requestValid: () -> Boolean
    ) {
        if (requestValid() && ConfigPreferences.getInstance(context).isShowNativeExit == true && requestNativeExitValid) {
            Log.d(TAG, "requestNativeExit ")
            AdmobFactory.getInstance().requestNativeAd(context, idAd, object : NativeAdCallback {
                override fun populateNativeAd() {

                }

                override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                    nativeExit.postValue(data)
                    requestNativeExitValid = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeExit.postValue(null)
                    requestNativeExitValid = true
                }

                override fun onAdClicked() {

                }

                override fun onAdImpression() {
                    requestNativeExitValid = true
                }

                override fun onAdFailedToShow(adError: AdError) {
                    requestNativeExitValid = true
                }
            })
        } else {
            requestNativeExitValid = true
            nativeExit.postValue(null)
            Log.e(TAG, "requestNativeExit: invalid")
        }
    }
    fun requestNativeOnboarding1(
        context: Context,
        idAd: String,
        requestValid: () -> Boolean
    ) {
        if (requestValid() && ConfigPreferences.getInstance(context).isShowNativeOnboarding == true) {
            Log.d(TAG, "requestNativeOnboarding1 ")
            AdmobFactory.getInstance().requestNativeAd(context, idAd, object : NativeAdCallback {
                override fun populateNativeAd() {

                }

                override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                    nativeOb1.postValue(data)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeOb1.postValue(null)
                }

                override fun onAdClicked() {

                }

                override fun onAdImpression() {

                }

                override fun onAdFailedToShow(adError: AdError) {

                }
            })
        } else {
            nativeOb1.postValue(null)
            Log.e(TAG, "requestNativeOnboarding1: invalid")
        }
    }

    fun requestNativeOnboarding2(
        context: Context,
        idAd: String,
        requestValid: () -> Boolean
    ) {
        if (requestValid() && ConfigPreferences.getInstance(context).isShowNativeOnboarding == true) {
            Log.d(TAG, "requestNativeOnboarding2 ")
            AdmobFactory.getInstance().requestNativeAd(context, idAd, object : NativeAdCallback {
                override fun populateNativeAd() {

                }

                override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                    nativeOb2.postValue(data)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeOb2.postValue(null)
                }

                override fun onAdClicked() {

                }

                override fun onAdImpression() {

                }

                override fun onAdFailedToShow(adError: AdError) {

                }
            })
        } else {
            nativeOb2.postValue(null)
            Log.e(TAG, "requestNativeOnboarding2: invalid")
        }
    }

    fun requestNativeOnboarding3(
        context: Context,
        idAd: String,
        requestValid: () -> Boolean
    ) {
        if (requestValid() && ConfigPreferences.getInstance(context).isShowNativeOnboarding == true) {
            Log.d(TAG, "requestNativeOnboarding3 ")
            AdmobFactory.getInstance().requestNativeAd(context, idAd, object : NativeAdCallback {
                override fun populateNativeAd() {

                }

                override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                    nativeOb3.postValue(data)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeOb3.postValue(null)
                }

                override fun onAdClicked() {

                }

                override fun onAdImpression() {

                }

                override fun onAdFailedToShow(adError: AdError) {

                }
            })
        } else {
            nativeOb3.postValue(null)
            Log.e(TAG, "requestNativeOnboarding3: invalid")
        }
    }

    fun requestNativeLFO1(
        context: Context,
        idAd: String,
        requestValid: () -> Boolean
    ) {
        if (requestValid() && ConfigPreferences.getInstance(context).isShowNativeLanguage == true) {
            Log.d(TAG, "requestNativeLFO1 ")
            AdmobFactory.getInstance().requestNativeAd(context, idAd, object : NativeAdCallback {
                override fun populateNativeAd() {

                }

                override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                    nativeLFO1.postValue(data)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeLFO1.postValue(null)
                }

                override fun onAdClicked() {

                }

                override fun onAdImpression() {

                }

                override fun onAdFailedToShow(adError: AdError) {

                }
            })
        } else {
            nativeLFO1.postValue(null)
            Log.e(TAG, "onAdLFO1FailedToLoad: invalid")
        }
    }

    fun requestNativeLFO2(
        context: Context,
        idAd: String,
        requestValid: () -> Boolean
    ) {
        if (requestValid() && ConfigPreferences.getInstance(context).isShowNativeLanguage == true) {
            Log.d(TAG, "requestNativeLFO2 ")
            AdmobFactory.getInstance().requestNativeAd(context, idAd, object : NativeAdCallback {
                override fun populateNativeAd() {

                }

                override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                    nativeLFO2.postValue(data)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeLFO2.postValue(null)
                }

                override fun onAdClicked() {

                }

                override fun onAdImpression() {

                }

                override fun onAdFailedToShow(adError: AdError) {

                }
            })
        } else {
            nativeLFO2.postValue(null)
            Log.e(TAG, "onAdLFO2FailedToLoad: invalid")
        }
    }

    fun requestLFO1Alternate(
        context: Context,
        idAdPriorityAd: String,
        idAdAllPrice: String,
        requestValid: () -> Boolean
    ) {
        if (requestValid() && ConfigPreferences.getInstance(context).isShowNativeLanguage == true) {
            requestNativeAlternate(
                context,
                idAdPriorityAd,
                idAdAllPrice,
                onAdLoaded = { nativeAd ->
                    nativeLFO1.postValue(nativeAd)
                },
                onFailedToLoad = {
                    nativeLFO1.postValue(null)
                },
                onAdImpression = {

                }
            ) {
            }
        } else {
            nativeLFO1.postValue(null)
        }
    }

    fun requestLFO2Alternate(
        context: Context,
        idAdPriorityAd: String,
        idAdAllPrice: String,
        requestValid: () -> Boolean
    ) {
        if (requestValid() && ConfigPreferences.getInstance(context).isShowNativeLanguage == true) {
            requestNativeAlternate(
                context,
                idAdPriorityAd,
                idAdAllPrice,
                onAdLoaded = { nativeAd ->
                    nativeLFO2.postValue(nativeAd)
                },
                onFailedToLoad = {
                    nativeLFO2.postValue(null)
                },
                onAdImpression = {
                }
            ) {

            }
        } else {
            nativeLFO2.postValue(null)
        }
    }

    private fun requestNativeAlternate(
        context: Context,
        idAdPriorityAd: String,
        idAdAllPrice: String,
        onAdLoaded: (ContentAd.AdmobAd.ApNativeAd) -> Unit,
        onFailedToLoad: () -> Unit,
        onAdImpression: () -> Unit = {},
        onClick: () -> Unit
    ) {
        requestNativeAd(
            context,
            idAdPriorityAd,
            onAdLoaded = { nativeAd ->
                Log.d(TAG, "requestNativeAlternate: Priority Loaded  $idAdPriorityAd")
                onAdLoaded(nativeAd)
            },
            onFailedToLoad = {
                Log.d(TAG, "requestNativeAlternate: Priority Failed To Load ")
                requestNativeAd(
                    context,
                    idAdAllPrice,
                    onAdLoaded = { nativeAd ->
                        Log.d(TAG, "requestNativeAlternate: All price loaded $idAdAllPrice")
                        onAdLoaded(nativeAd)
                    },
                    onFailedToLoad = {
                        Log.d(TAG, "requestNativeAlternate: All price Failed To Load ")
                        onFailedToLoad()
                    },
                    onAdImpression = onAdImpression,
                    onClick = onClick
                )
            },
            onClick = onClick
        )
    }

    private fun requestNativeAd(
        context: Context,
        idAd: String,
        onAdLoaded: (ContentAd.AdmobAd.ApNativeAd) -> Unit,
        onFailedToLoad: () -> Unit,
        onAdImpression: () -> Unit = {},
        onClick: () -> Unit
    ) {
        AdmobFactory.getInstance().requestNativeAd(context, idAd, object : NativeAdCallback {
            override fun populateNativeAd() {

            }

            override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                onAdLoaded(data)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                onFailedToLoad()
            }

            override fun onAdClicked() {
                onClick()
            }

            override fun onAdImpression() {
                onAdImpression()
            }

            override fun onAdFailedToShow(adError: AdError) {

            }
        })

    }

}
