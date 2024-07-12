package com.noatnoat.pianoapp.admob.lfo

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdConfig
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdHelper
import com.noatnoat.pianoapp.admob.helper.adnative.params.NativeAdParam
import com.noatnoat.pianoapp.admob.listener.NativeAdCallback
import com.noatnoat.pianoapp.extensions.findNavControllerSafely
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.utils.AnalyticsUtil

/**
 * Created by ViO on 17/03/2024.
 */
class LFOSelectFragment : BaseLFOFragment(), LFOSelectLanguage {

    private val nativeAdHelper by lazy { initNativeAd() }

    private fun initNativeAd(): NativeAdHelper? {
        myActivity?.let {
            val idNativeAd = if (ConfigPreferences.getInstance(it).isShowLfo2HighFloor == true) {
                getString(R.string.NATIVE_GG_LANGUAGE_S2_2F)
            } else {
                getString(R.string.NATIVE_GG_LANGUAGE_S2)
            }
            val config = NativeAdConfig(
                idNativeAd,
                canShowAds = ConfigPreferences.getInstance(it).isShowNativeLanguage == true,
                canReloadAds = true,
                layoutId = R.layout.layout_native_big
            )
            return NativeAdHelper(it, this, config).apply {
                if (ConfigPreferences.getInstance(it).isShowLfo2HighFloor == true) {
                    registerAdListener(object : NativeAdCallback {
                        override fun populateNativeAd() {

                        }

                        override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {

                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            NativeUtils.requestNativeLFO2(it, getString(R.string.NATIVE_GG_LANGUAGE_S2)) {
                                true
                            }
                        }

                        override fun onAdClicked() {

                        }

                        override fun onAdImpression() {

                        }

                        override fun onAdFailedToShow(adError: AdError) {

                        }

                    })
                }
            }
        }
        return null
    }


    override fun initView() {
        initAdapter()
        arguments?.getInt(KEY_SELECT_POSITION)?.let {
            lfoAdapter?.let { adapter ->
                updateUiSelect(adapter.getListData()[it])

            }
        }
    }

    override fun onResume() {
        super.onResume()
        val scrollY = arguments?.getInt(KEY_SCROLL_Y) ?: 0
        binding?.recyclerView?.post {
            binding?.recyclerView?.scrollBy(0, scrollY)
        }
    }


    private fun initAdapter() {
        lfoAdapter?.registerListener(this)
        binding?.recyclerView?.adapter = lfoAdapter
    }

    override fun onSelectLanguage(language: Language) {
        updateUiSelect(language)
    }

    private fun updateUiSelect(language: Language) {
        val positionSelected = lfoAdapter?.getListData()?.indexOf(language)
        val positionUnSelect = lfoAdapter?.getListData()?.indexOfFirst { it.isChoose }
        lfoAdapter?.getListData()?.find { it.isChoose }?.isChoose = false
        language.isChoose = true
        positionSelected?.let {
            lfoAdapter?.notifyItemChanged(positionSelected)
        }
        positionUnSelect?.let {
            lfoAdapter?.notifyItemChanged(positionUnSelect)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNativeAd()
        binding?.imgChooseLanguage?.setOnClickListener {
            AnalyticsUtil.logEvent(AnalyticsUtil.LANGUAGE_FO_SAVE_CLICK)

            lfoAdapter?.getLanguageSelected()?.let { language ->
                myActivity?.let { it1 ->
                    LanguageUtils.changeLang(language.code, it1)
                    NativeUtils.requestNativeOnboarding2(it1, getString(R.string.NATIVE_GG_ONBOARDING)) {
                        true
                    }
                    findNavControllerSafely(R.id.LFOSelectFragment)?.navigate(R.id.actionLFOSelectToIntro)
                }

            } ?: run {
                myActivity?.let { it1 ->
                    NativeUtils.requestNativeOnboarding2(it1, getString(R.string.NATIVE_GG_ONBOARDING)) {
                        true
                    }
                    findNavControllerSafely(R.id.LFOSelectFragment)?.navigate(R.id.actionLFOSelectToIntro)
                }
            }
        }
    }

    private fun setupNativeAd() {
        binding?.layoutAdNative?.isVisible = true
        binding?.layoutAdNative?.let { nativeAdHelper?.setNativeContentView(it) }
        binding?.includeShimmer?.shimmerContainerNative?.let {
            nativeAdHelper?.setShimmerLayoutView(it)
        }
        NativeUtils.nativeLFO2.observe(viewLifecycleOwner) { nativeAd ->
            if (nativeAd != null) {
                nativeAdHelper?.requestAds(NativeAdParam.Ready(nativeAd.nativeAd))
            } else {
                nativeAdHelper?.requestAds(NativeAdParam.Request.create())
            }
        }
    }
}
