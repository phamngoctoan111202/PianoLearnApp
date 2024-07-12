package com.noatnoat.pianoapp.admob.lfo

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toolbar
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

/**
 * Created by ViO on 17/03/2024.
 */
class LFOFragment : BaseLFOFragment(), LFOSelectLanguage {

    private val nativeAdHelper by lazy { initNativeAd() }
    private fun initNativeAd(): NativeAdHelper? {
        myActivity?.let {
            val idNativeAd = if (ConfigPreferences.getInstance(it).isShowLfo1HighFloor == true) {
                getString(R.string.NATIVE_GG_LANGUAGE_2F)
            } else {
                getString(R.string.NATIVE_GG_LANGUAGE)
            }
            val config = NativeAdConfig(
                idNativeAd,
                canShowAds = ConfigPreferences.getInstance(it).isShowNativeLanguage == true,
                canReloadAds = true,
                layoutId = R.layout.layout_native_big
            )
            return NativeAdHelper(it, this, config).apply {
                if (ConfigPreferences.getInstance(it).isShowLfo1HighFloor == true) {
                    registerAdListener(object : NativeAdCallback {
                        override fun populateNativeAd() {

                        }

                        override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {

                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            NativeUtils.requestNativeLFO1(it, getString(R.string.NATIVE_GG_LANGUAGE)) {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNativeAd()
        binding?.imgChooseLanguage?.setOnClickListener {
            lfoAdapter?.getLanguageSelected()?.let { language ->
                myActivity?.let { it1 ->
                    LanguageUtils.changeLang(language.code, it1)
                    NativeUtils.requestNativeOnboarding2(it1, getString(R.string.NATIVE_GG_ONBOARDING)) {
                        true
                    }
                }
                findNavControllerSafely(R.id.LFOFragment)?.navigate(R.id.actionLfoToIntro)
            } ?: run {
                myActivity?.let { it1 ->
                    NativeUtils.requestNativeOnboarding2(it1, getString(R.string.NATIVE_GG_ONBOARDING)) {
                        true
                    }
                }
                findNavControllerSafely(R.id.LFOFragment)?.navigate(R.id.actionLfoToIntro)
            }
        }
        binding?.imgChooseLanguage?.isEnabled = false
        binding?.imgChooseLanguage?.alpha = 0.5f

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            activity?.window?.statusBarColor = Color.TRANSPARENT
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        }

    }

    private fun setupNativeAd() {
        myActivity?.let { myActivity ->
            if (ConfigPreferences.getInstance(myActivity).isShowNativeLanguage == true) {
                binding?.layoutAdNative?.let { nativeAdHelper?.setNativeContentView(it) }
                binding?.includeShimmer?.shimmerContainerNative?.let {
                    nativeAdHelper?.setShimmerLayoutView(it)

                }
                NativeUtils.nativeLFO1.observe(viewLifecycleOwner) { nativeAd ->
                    if (nativeAd != null) {
                        nativeAdHelper?.requestAds(NativeAdParam.Ready(nativeAd.nativeAd))
                    } else {
                        nativeAdHelper?.requestAds(NativeAdParam.Request.create())
                    }
                }

                //request native LFO2
                if (ConfigPreferences.getInstance(myActivity).isShowLfo2HighFloor == true) {
                    NativeUtils.requestLFO2Alternate(
                        myActivity,
                        getString(R.string.NATIVE_GG_LANGUAGE_S2_2F),
                        getString(R.string.NATIVE_GG_LANGUAGE_S2)
                    ) {
                        true
                    }
                } else {
                    NativeUtils.requestNativeLFO2(
                        myActivity,
                        getString(R.string.NATIVE_GG_LANGUAGE_S2)
                    ) {
                        true
                    }
                }
            } else {
                binding?.layoutAdNative?.isVisible = false
            }

            //request native Onboarding 1
            NativeUtils.requestNativeOnboarding1(myActivity, getString(R.string.NATIVE_GG_ONBOARDING)) {
                true
            }
        }

    }

    private fun initAdapter() {
        lfoAdapter?.registerListener(this)
        binding?.recyclerView?.adapter = lfoAdapter
    }

    override fun onSelectLanguage(language: Language) {
        binding?.imgChooseLanguage?.isEnabled = true
        binding?.imgChooseLanguage?.alpha = 1f

        binding?.recyclerView?.computeVerticalScrollOffset()?.let {
            lfoAdapter?.getListData()?.indexOf(language)?.let { it1 ->
                navigateToSelect(
                    it1,
                    it
                )
            }
        }
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

}
