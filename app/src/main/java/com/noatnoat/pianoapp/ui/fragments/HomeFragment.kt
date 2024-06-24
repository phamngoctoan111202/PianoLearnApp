package com.noatnoat.pianoapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.noatnoat.pianoapp.R

import com.noatnoat.pianoapp.admob.BannerCollapsibleGravity
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdConfig
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdHelper
import com.noatnoat.pianoapp.admob.helper.adnative.params.NativeAdParam
import com.noatnoat.pianoapp.admob.helper.banner.BannerAdConfig
import com.noatnoat.pianoapp.admob.helper.banner.BannerAdHelper
import com.noatnoat.pianoapp.admob.helper.banner.params.BannerAdParam
import com.noatnoat.pianoapp.admob.helper.interstitial.InterstitialAdConfig
import com.noatnoat.pianoapp.admob.helper.interstitial.InterstitialAdHelper
import com.noatnoat.pianoapp.admob.helper.interstitial.params.InterstitialAdParam
import com.noatnoat.pianoapp.admob.listener.InterstitialAdCallback
import com.noatnoat.pianoapp.databinding.FragmentHomeBinding
import com.noatnoat.pianoapp.utils.Helper

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    // for native
    private val nativeAdHelper by lazy { initNativeAd() }
    private fun initNativeAd(): NativeAdHelper? {
        activity?.let { myActivity ->
            val config = NativeAdConfig(
                idAds = getString(R.string.NATIVE_HOME),
                canShowAds = ConfigPreferences.getInstance(myActivity).isShowNativeHome == true,
                canReloadAds = true,
                layoutId = R.layout.layout_native_medium
            )
            return NativeAdHelper(myActivity, this, config)
        }
        return null
    }

    // for banner
    private val bannerAdHelper by lazy { initBannerAd() }

    // for interstitial
    private val interAdHelper by lazy { initInterAdAd() }
    private var navigateToScreen = -1

    private fun initInterAdAd(): InterstitialAdHelper? {
        activity?.let { myActivity ->
            val config = InterstitialAdConfig(
                idAds = getString(R.string.INTER_GG_HOME),
                canShowAds = ConfigPreferences.getInstance(myActivity).isShowInterHome == true,
                canReloadAds = true,
                showByTime = 2
            )
            return InterstitialAdHelper(
                activity = myActivity,
                lifecycleOwner = this,
                config = config
            )
        }
        return null
    }

    annotation class NavigateToScreen {
        companion object {
            const val MOVE_SCREEN_1 = 0
            const val MOVE_SCREEN_2 = 1
            const val MOVE_SCREEN_3 = 1

        }
    }

    private fun initBannerAd(): BannerAdHelper? {
        activity?.let { myActivity ->
            val config = BannerAdConfig(
                idAds = getString(R.string.BANNER_GG_COLLAP),
                canShowAds = ConfigPreferences.getInstance(myActivity).isShowBannerCollapsibleHome == true,
                canReloadAds = true,
            ).apply {
                collapsibleGravity = BannerCollapsibleGravity.BOTTOM
            }
            return BannerAdHelper(activity = myActivity, lifecycleOwner = this, config = config)
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // for interstitial
        interAdHelper?.requestAds(InterstitialAdParam.Request)
        interAdHelper?.registerAdListener(object : InterstitialAdCallback {
            override fun onNextAction() {
                handleNavigateTo()
            }

            override fun onAdClose() {
                handleNavigateTo()
            }

            override fun onInterstitialShow() {
            }

            override fun onAdLoaded(data: ContentAd.AdmobAd.ApInterstitialAd) {
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            }

            override fun onAdClicked() {
            }

            override fun onAdImpression() {
            }

            override fun onAdFailedToShow(adError: AdError) {
            }
        })

    }

    private fun handleNavigateTo() {
        when (navigateToScreen) {
            NavigateToScreen.MOVE_SCREEN_1 -> {
                Helper.showToast(activity, "Move to screen 1", true)
            }

            NavigateToScreen.MOVE_SCREEN_2 -> {
                Helper.showToast(activity, "Move to screen 2", true)
            }

            NavigateToScreen.MOVE_SCREEN_3 -> {
                Helper.showToast(activity, "Move to screen 3", true)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        interAdHelper?.unregisterAllAdListener()
    }


    companion object {
        var isShowHome = false;
    }

    override fun onPause() {
        super.onPause()
        isShowHome = false;
    }

    override fun onResume() {
        super.onResume()
        isShowHome = true;
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(view)
        binding.buttonNavigateToHomeFragment2.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToHomeFragment2()
            findNavController().navigate(action)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {
        binding.apply {
            btShowInter.setOnClickListener {
                if (!Helper.isDoubleClick()) {
                    navigateToScreen = NavigateToScreen.MOVE_SCREEN_1
                    interAdHelper?.requestAds(InterstitialAdParam.ShowAd)
                }
            }

            btShowNative.setOnClickListener {
                nativeAdHelper?.setNativeContentView(binding.flNativeAds)
                nativeAdHelper?.setShimmerLayoutView(binding.includeShimmer.shimmerContainerNative)
                nativeAdHelper?.requestAds(NativeAdParam.Request.create())
            }

            btShowBannerCollap.setOnClickListener {
                bannerAdHelper?.setBannerContentView(binding.frAds)
                if (bannerAdHelper?.bannerAdView == null) {
                    bannerAdHelper?.requestAds(BannerAdParam.Request)
                }
            }
        }
    }

}