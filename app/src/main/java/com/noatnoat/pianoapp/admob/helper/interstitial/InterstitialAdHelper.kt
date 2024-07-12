package com.noatnoat.pianoapp.admob.helper.interstitial

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.noatnoat.pianoapp.admob.AdmobManager
import com.noatnoat.pianoapp.admob.admob.AdmobFactory
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.dialog.LoadingAdsDialog
import com.noatnoat.pianoapp.admob.helper.AdsHelper
import com.noatnoat.pianoapp.admob.helper.interstitial.params.AdInterstitialState
import com.noatnoat.pianoapp.admob.helper.interstitial.params.InterstitialAdParam
import com.noatnoat.pianoapp.admob.listener.InterstitialAdCallback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
/**
 * Created by ViO on 16/03/2024.
 */
class InterstitialAdHelper(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
    private val config: InterstitialAdConfig
) : AdsHelper<InterstitialAdConfig, InterstitialAdParam>(activity, lifecycleOwner, config) {
    private val listAdCallback: CopyOnWriteArrayList<InterstitialAdCallback> =
        CopyOnWriteArrayList()
    private val adInterstitialState: MutableStateFlow<AdInterstitialState> =
        MutableStateFlow(if (canRequestAds()) AdInterstitialState.None else AdInterstitialState.Fail)
    var interstitialAdValue: InterstitialAd? = null
        private set
    private var requestShowCount = 0

    private val dialogLoading by lazy { LoadingAdsDialog(activity) }
    private var loadingJob: Job? = null

    override fun requestAds(param: InterstitialAdParam) {
        lifecycleOwner.lifecycleScope.launch {
            if (canRequestAds()) {
                when (param) {
                    is InterstitialAdParam.Request -> {
                        flagActive.compareAndSet(false, true)
                        createInterAds(activity)
                    }

                    is InterstitialAdParam.Show -> {
                        flagActive.compareAndSet(false, true)
                        interstitialAdValue = param.interstitialAd
                          showInterAds(activity)
                    }

                    is InterstitialAdParam.ShowAd -> {
                        flagActive.compareAndSet(false, true)
                          showInterAds(activity)
                    }

                    else -> {

                    }
                }
            } else {
                invokeAdListener { it.onNextAction() }
            }
        }
    }

    private fun showInterAds(activity: Activity) {
        if (config.showByTime != 1) {
            requestShowCount++
        }
        if (requestShowCount % config.showByTime == 0 && interstitialAdValue != null) {
            lifecycleOwner.lifecycleScope.launch {
                AdmobManager.adsShowFullScreen()
                showDialogLoading()
                delay(800)
                AdmobFactory.getInstance()
                    .showInterstitial(activity, interstitialAdValue, invokeListenerAdCallback())
                loadingJob = lifecycleOwner.lifecycleScope.launch {
                    delay(2000)
                    dialogLoading.dismiss()
                }
            }
        } else if (requestShowCount % config.showByTime ==
            if (config.showByTime <= 2) {
                1
            } else {
                config.showByTime - 1
            }
            && adInterstitialState.value != AdInterstitialState.Loading
        ) {
            invokeAdListener { it.onNextAction() }
            requestAds(InterstitialAdParam.Request)
        } else {
            invokeAdListener { it.onNextAction() }
        }
    }

    private fun showDialogLoading() {
        dialogLoading.show()
    }

    private fun requestValid(): Boolean {
        val showConfigValid = (config.showByTime == 1 || requestShowCount % config.showByTime ==
                if (config.showByTime <= 2) {
                    1
                } else {
                    config.showByTime - 1
                })
        val valueValid =
            (interstitialAdValue == null && adInterstitialState.value != AdInterstitialState.Loading) || adInterstitialState.value == AdInterstitialState.Showed
        return canRequestAds() && showConfigValid && valueValid
    }

    private fun createInterAds(activity: Activity) {
        if (requestValid()) {
            lifecycleOwner.lifecycleScope.launch {
                adInterstitialState.emit(AdInterstitialState.Loading)
                AdmobFactory.getInstance()
                    .requestInterstitialAds(
                        activity,
                        config.idAds,
                        invokeListenerAdCallback()
                    )
            }
        }
    }

    override fun cancel() {
    }

    fun registerAdListener(adCallback: InterstitialAdCallback) {
        this.listAdCallback.add(adCallback)
    }

    fun unregisterAdListener(adCallback: InterstitialAdCallback) {
        this.listAdCallback.remove(adCallback)
    }

    fun unregisterAllAdListener() {
        this.listAdCallback.clear()
    }

    private fun invokeAdListener(action: (adCallback: InterstitialAdCallback) -> Unit) {
        listAdCallback.forEach(action)
    }

    private fun invokeListenerAdCallback(): InterstitialAdCallback {
        return object : InterstitialAdCallback {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                invokeAdListener { it.onAdFailedToLoad(loadAdError) }
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Fail)
                }
            }

            override fun onAdLoaded(data: ContentAd.AdmobAd.ApInterstitialAd) {
                Log.e(TAG, "onInterstitialLoad: ")
                interstitialAdValue = data.interstitialAd
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Loaded)
                }
                invokeAdListener { it.onAdLoaded(data) }
            }


            override fun onAdFailedToShow(adError: AdError) {
                AdmobManager.adsFullScreenDismiss()
                invokeAdListener { it.onNextAction() }
                dialogLoading.dismiss()
                cancelLoadingJob()
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.ShowFail)
                }
            }

            override fun onNextAction() {
                AdmobManager.adsFullScreenDismiss()
                dialogLoading.dismiss()
                cancelLoadingJob()
                invokeAdListener { it.onNextAction() }
            }

            override fun onAdClose() {
                AdmobManager.adsFullScreenDismiss()
                dialogLoading.dismiss()
                cancelLoadingJob()
                invokeAdListener { it.onAdClose() }
            }

            override fun onInterstitialShow() {
                AdmobManager.adsShowFullScreen()
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Showed)
                }
                if (config.canReloadAds) {
                    requestAds(InterstitialAdParam.Request)
                }
            }

            override fun onAdClicked() {
                invokeAdListener { it.onAdClicked() }
            }

            override fun onAdImpression() {
                invokeAdListener { it.onAdImpression() }
            }

        }
    }

    private fun cancelLoadingJob() {
        loadingJob?.cancel()
        loadingJob = null
    }

    companion object {
        private val TAG = InterstitialAdHelper::class.simpleName
    }
}