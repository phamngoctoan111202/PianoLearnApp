package com.noatnoat.pianoapp.admob.helper.banner

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.admob.admob.AdmobFactory
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.helper.AdsHelper
import com.noatnoat.pianoapp.admob.helper.banner.params.AdBannerState
import com.noatnoat.pianoapp.admob.helper.banner.params.BannerAdParam
import com.noatnoat.pianoapp.admob.listener.BannerAdCallBack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by ViO on 16/03/2024.
 */
class BannerAdHelper(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
    private val config: BannerAdConfig,
) : AdsHelper<BannerAdConfig, BannerAdParam>(activity, lifecycleOwner, config) {

    private val adBannerState: MutableStateFlow<AdBannerState> =
        MutableStateFlow(if (canRequestAds()) AdBannerState.None else AdBannerState.Fail)
    private var timeShowAdImpression: Long = 0
    private val listAdCallback: CopyOnWriteArrayList<BannerAdCallBack> = CopyOnWriteArrayList()
    private val resumeCount: AtomicInteger = AtomicInteger(0)
    private var shimmerLayoutView: ShimmerFrameLayout? = null
    private var bannerContentView: FrameLayout? = null
    private var isRequestValid = true
    var bannerAdView: AdView? = null
        private set

    init {
        registerAdListener(getDefaultCallback())
        lifecycleEventState.onEach {
            if (it == Lifecycle.Event.ON_CREATE) {
                if (!canRequestAds()) {
                    bannerContentView?.isVisible = false
                    shimmerLayoutView?.isVisible = false
                }
            }

            if (it == Lifecycle.Event.ON_RESUME) {
                if (!canShowAds() && isActiveState()) {
                    cancel()
                }
            }
        }.launchIn(lifecycleOwner.lifecycleScope)
        //Request when resume
        lifecycleEventState.debounce(300).onEach { event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                resumeCount.incrementAndGet()
                logZ("Resume repeat ${resumeCount.get()} times")
                if (!isActiveState()) {
                    logInterruptExecute("Request when resume")
                }
            }
            if (event == Lifecycle.Event.ON_RESUME && resumeCount.get() > 1 && bannerAdView != null && canRequestAds() && canReloadAd() && isActiveState()) {
                if (!isRequestValid){
                    isRequestValid = true
                    return@onEach
                }
                logZ("requestAds on resume")
                requestAds(BannerAdParam.Request)
            }
        }.launchIn(lifecycleOwner.lifecycleScope)
        //for action resume or init
        adBannerState
            .onEach { logZ("adBannerState(${it::class.java.simpleName})") }
            .launchIn(lifecycleOwner.lifecycleScope)
        adBannerState.onEach { adsParam ->
            handleShowAds(adsParam)
        }.launchIn(lifecycleOwner.lifecycleScope)
    }

    fun getBannerState(): Flow<AdBannerState> {
        return adBannerState.asStateFlow()
    }

    private fun handleShowAds(adsParam: AdBannerState) {
        bannerContentView?.isGone = adsParam is AdBannerState.Cancel || !canShowAds()
        shimmerLayoutView?.isVisible = adsParam is AdBannerState.Loading
        when (adsParam) {
            is AdBannerState.Loaded -> {
                val bannerContentView = bannerContentView
                val shimmerLayoutView = shimmerLayoutView
                if (bannerContentView != null && shimmerLayoutView != null) {
                    bannerContentView.setBackgroundColor(Color.WHITE)
                    val view = View(bannerContentView.context)
                    val oldHeight = bannerContentView.height
                    bannerContentView.let {
                        it.removeAllViews()
                        it.addView(view, 0, oldHeight)
                        it.addView(adsParam.adBanner)
                    }
                }
            }

            else -> Unit
        }
    }

    override fun requestAds(param: BannerAdParam) {
        logZ("requestAds with param:${param::class.java.simpleName}")
        if (canRequestAds()) {
            lifecycleOwner.lifecycleScope.launch {
                when (param) {
                    is BannerAdParam.Request -> {
                        flagActive.compareAndSet(false, true)
                        if (bannerAdView == null) {
                            adBannerState.emit(AdBannerState.Loading)
                        }
                        loadBannerAd()
                    }

                    is BannerAdParam.Ready -> {
                        flagActive.compareAndSet(false, true)
                        adBannerState.emit(AdBannerState.Loaded(param.bannerAds))
                    }

                    is BannerAdParam.Clickable -> {
                        if (isActiveState()) {
                            if (timeShowAdImpression + param.minimumTimeKeepAdsDisplay < System.currentTimeMillis()) {
                                loadBannerAd()
                            }
                        } else {
                            logInterruptExecute("requestAds Clickable")
                        }
                    }
                }
            }
        } else {
            if (!isOnline() && bannerAdView == null) {
                cancel()
            }
        }
    }

    override fun cancel() {
        logZ("cancel() called")
        flagActive.compareAndSet(true, false)
        bannerAdView = null
        lifecycleOwner.lifecycleScope.launch { adBannerState.emit(AdBannerState.Cancel) }
    }

    private fun loadBannerAd() {
        if (canRequestAds()) {
            AdmobFactory.getInstance()
                .requestBannerAd(
                    activity,
                    config.idAds,
                    config.collapsibleGravity,
                    invokeListenerAdCallback()
                )
        }
    }

    fun setShimmerLayoutView(shimmerLayoutView: ShimmerFrameLayout) = apply {
        kotlin.runCatching {
            this.shimmerLayoutView = shimmerLayoutView
            if (lifecycleOwner.lifecycle.currentState in Lifecycle.State.CREATED..Lifecycle.State.RESUMED) {
                if (!canRequestAds()) {
                    shimmerLayoutView.isVisible = false
                }
            }
        }
    }

    fun setBannerContentView(nativeContentView: FrameLayout) = apply {
        kotlin.runCatching {
            this.bannerContentView = nativeContentView
            this.shimmerLayoutView =
                nativeContentView.findViewById(R.id.shimmer_container_banner)
            if (lifecycleOwner.lifecycle.currentState in Lifecycle.State.CREATED..Lifecycle.State.RESUMED) {
                if (!canRequestAds()) {
                    nativeContentView.isVisible = false
                    shimmerLayoutView?.isVisible = false
                }
            }
        }
    }

    private fun getDefaultCallback(): BannerAdCallBack {
        return object : BannerAdCallBack {
            override fun onAdLoaded(data: ContentAd.AdmobAd.ApBannerAd) {
                if (isActiveState()) {
                    lifecycleOwner.lifecycleScope.launch {
                        bannerAdView = data.adView
                        adBannerState.emit(AdBannerState.Loaded(data.adView))
                    }
                    logZ("onBannerLoaded()")
                } else {
                    logInterruptExecute("onBannerLoaded")
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                if (isActiveState()) {
                    lifecycleOwner.lifecycleScope.launch {
                        adBannerState.emit(AdBannerState.Fail)
                    }
                    logZ("onAdFailedToLoad()")
                } else {
                    logInterruptExecute("onAdFailedToLoad")
                }
            }

            override fun onAdClicked() {
                invokeAdListener { it.onAdClicked() }
            }

            override fun onAdImpression() {
                logZ("Banner onAdImpression ${lifecycleOwner.lifecycle.currentState}")
                isRequestValid = lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED
                invokeAdListener { it.onAdImpression() }
            }

            override fun onAdFailedToShow(adError: AdError) {
                invokeAdListener { it.onAdFailedToShow(adError) }
            }

        }
    }

    fun registerAdListener(adCallback: BannerAdCallBack) {
        this.listAdCallback.add(adCallback)
    }

    fun unregisterAdListener(adCallback: BannerAdCallBack) {
        this.listAdCallback.remove(adCallback)
    }

    fun unregisterAllAdListener() {
        this.listAdCallback.clear()
    }

    private fun invokeListenerAdCallback(): BannerAdCallBack {
        return object : BannerAdCallBack {
            override fun onAdLoaded(data: ContentAd.AdmobAd.ApBannerAd) {
                invokeAdListener { it.onAdLoaded(data) }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                invokeAdListener { it.onAdFailedToLoad(loadAdError) }
            }

            override fun onAdClicked() {
                invokeAdListener { it.onAdClicked() }
            }

            override fun onAdImpression() {
                invokeAdListener { it.onAdImpression() }
            }

            override fun onAdFailedToShow(adError: AdError) {
                invokeAdListener { it.onAdFailedToShow(adError) }
            }
        }
    }

    private fun invokeAdListener(action: (adCallback: BannerAdCallBack) -> Unit) {
        listAdCallback.forEach(action)
    }
}