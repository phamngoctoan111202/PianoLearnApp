package com.noatnoat.pianoapp.admob.helper.interstitial


import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
class InterstitialAdSplashHelper(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
    private val config: InterstitialAdSplashConfig
) : AdsHelper<InterstitialAdSplashConfig, InterstitialAdParam>(activity, lifecycleOwner, config) {
    private val dialogLoading by lazy { LoadingAdsDialog(activity) }

    private val listAdCallback: CopyOnWriteArrayList<InterstitialAdCallback> =
        CopyOnWriteArrayList()
    private val adInterstitialState: MutableStateFlow<AdInterstitialState> =
        MutableStateFlow(if (canRequestAds()) AdInterstitialState.None else AdInterstitialState.Fail)
    var interstitialAdValue: InterstitialAd? = null
        private set

    private var requestTimeOutJob: Job? = null
    private var requestDelayJob: Job? = null
    private var showValid = false
    private var loadingJob: Job? = null
    override fun requestAds(param: InterstitialAdParam) {
        lifecycleOwner.lifecycleScope.launch {
            if (canRequestAds()) {
                when (param) {
                    is InterstitialAdParam.Request -> {
                        flagActive.compareAndSet(false, true)
                        if (interstitialAdValue == null) {
                            adInterstitialState.emit(AdInterstitialState.Loading)
                        }
                        createInterAds(activity)
                    }

                    is InterstitialAdParam.Show -> {
                        flagActive.compareAndSet(false, true)
                        interstitialAdValue = param.interstitialAd
                        adInterstitialState.emit(AdInterstitialState.Loaded)
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
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (adInterstitialState.value == AdInterstitialState.Loaded || adInterstitialState.value == AdInterstitialState.ShowFail) {
                    requestDelayJob?.cancel()
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
            }
        }
    }

    private fun showDialogLoading() {
        cancelLoadingJob()
        dialogLoading.show()
    }

    private fun createInterAds(activity: Activity) {
        requestTimeOutJob = lifecycleOwner.lifecycleScope.launch {
            AdmobFactory.getInstance()
                .requestInterstitialAds(activity, config.idAds, invokeListenerAdCallback())
            delay(config.timeOut)
            if (interstitialAdValue != null && config.showReady) {
                showInterAds(activity)
            } else {
                invokeAdListener { it.onNextAction() }
                requestTimeOutJob?.cancel()
            }
        }
        requestDelayJob = lifecycleOwner.lifecycleScope.launch {
            delay(config.timeDelay)
            showValid = true
            if (interstitialAdValue != null && config.showReady) {
                showInterAds(activity)
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
            override fun onNextAction() {
                dialogLoading.dismiss()
                cancelLoadingJob()
                AdmobManager.adsFullScreenDismiss()
                invokeAdListener { it.onNextAction() }
            }

            override fun onAdClose() {
                dialogLoading.dismiss()
                cancelLoadingJob()
                invokeAdListener { it.onAdClose() }
                AdmobManager.adsFullScreenDismiss()
            }

            override fun onInterstitialShow() {
                AdmobManager.adsShowFullScreen()
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Showed)
                }
                requestTimeOutJob?.cancel()
            }

            override fun onAdLoaded(data: ContentAd.AdmobAd.ApInterstitialAd) {
                interstitialAdValue = data.interstitialAd
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Loaded)
                }
                if (showValid && config.showReady) {
                    showInterAds(activity)
                } else {
                    invokeAdListener { it.onAdLoaded(data) }
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                invokeAdListener { it.onNextAction() }
                invokeAdListener { it.onAdFailedToLoad(loadAdError) }
            }

            override fun onAdClicked() {
                invokeAdListener { it.onAdClicked() }
            }

            override fun onAdImpression() {
                invokeAdListener { it.onAdImpression() }
            }

            override fun onAdFailedToShow(adError: AdError) {
                AdmobManager.adsFullScreenDismiss()
                dialogLoading.dismiss()
                cancelLoadingJob()
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.ShowFail)
                }
                invokeAdListener { it.onAdFailedToShow(adError) }
                if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    invokeAdListener { it.onNextAction() }
                }
            }

        }
    }

    private fun cancelLoadingJob() {
        loadingJob?.cancel()
        loadingJob = null
    }

    companion object {
        private val TAG = InterstitialAdSplashHelper::class.simpleName
    }
}