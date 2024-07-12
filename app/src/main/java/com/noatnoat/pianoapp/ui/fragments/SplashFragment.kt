package com.noatnoat.pianoapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.noatnoat.pianoapp.utils.AnalyticsUtil
import com.noatnoat.pianoapp.PianoApplication
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.SharedPreferencesManager
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.cmp.ConsentManager
import com.noatnoat.pianoapp.admob.cmp.interfaces.OnConsentResponse
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.helper.banner.BannerAdConfig
import com.noatnoat.pianoapp.admob.helper.banner.BannerAdHelper
import com.noatnoat.pianoapp.admob.helper.banner.params.BannerAdParam
import com.noatnoat.pianoapp.admob.helper.interstitial.InterstitialAdSplashConfig
import com.noatnoat.pianoapp.admob.helper.interstitial.InterstitialAdSplashHelper
import com.noatnoat.pianoapp.admob.helper.interstitial.params.InterstitialAdParam
import com.noatnoat.pianoapp.admob.listener.InterstitialAdCallback
import com.noatnoat.pianoapp.checkPermission
import com.noatnoat.pianoapp.databinding.FragmentSplashBinding
import com.noatnoat.pianoapp.admob.lfo.NativeUtils
import com.noatnoat.pianoapp.utils.Helper
import com.noatnoat.pianoapp.extensions.findNavControllerSafely
import com.noatnoat.pianoapp.splash.SplashViewModel
import com.noatnoat.pianoapp.splash.SplashViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var viewModel: SplashViewModel

    @Inject
    lateinit var viewModelFactory: SplashViewModelFactory

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val interAdSplashHelper by lazy { initInterAdSplash() }
    private val consentManager by lazy { activity?.let { ConsentManager.getInstance(it) } }

    private val bannerAdHelper by lazy { initBannerAd() }

    private fun initBannerAd(): BannerAdHelper? {
        activity?.let { myActivity ->
            val config = BannerAdConfig(
                idAds = getString(R.string.BANNER_GG_SPLASH),
                canShowAds = ConfigPreferences.getInstance(myActivity).isShowBanner == true,
                canReloadAds = true,
            )
            return BannerAdHelper(activity = myActivity, lifecycleOwner = this, config = config)
        }
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bannerAdHelper?.setBannerContentView(binding.frAds)
        bannerAdHelper?.requestAds(BannerAdParam.Request.create())
    }

    private fun initInterAdSplash(): InterstitialAdSplashHelper? {
        return activity?.let { myActivity ->
            val config = InterstitialAdSplashConfig(
                idAds = getString(R.string.INTER_GG_SPLASH),
                canShowAds = consentManager?.getConsentResult(myActivity) ?: true
                        && ConfigPreferences.getInstance(myActivity).isShowInterSplash == true,
                canReloadAds = true,
                timeDelay = 5000L,
                timeOut = 30000L,
                showReady = true
            )
            InterstitialAdSplashHelper(
                activity = myActivity,
                lifecycleOwner = this,
                config = config
            ).apply {
                registerAdListener(object : InterstitialAdCallback {
                    override fun onNextAction() {
                        Helper.myLog("Splash onNextAction")
                        handleOpenScreens(isOpenLFO(), isOpenOnboarding())
                    }

                    override fun onAdClose() {
                        handleOpenScreens(isOpenLFO(), isOpenOnboarding())
                    }

                    override fun onInterstitialShow() {
                    }

                    override fun onAdLoaded(data: ContentAd.AdmobAd.ApInterstitialAd) {
                        Helper.myLog("Splash onAdLoaded")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Helper.myLog("Splash onAdFailedToLoad")
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        MainActivity.isShowVideoAd = true
        isLive = true

        PianoApplication.fetchRemoteConfigComplete.observe(this) {
            if (it) {
                if (context?.let { ConfigPreferences.getInstance(it).isEnableUMP } == true) {
                    consentManager?.initReleaseConsent(onConsentResponse = object :
                        OnConsentResponse {
                        override fun onResponse(errorMessage: String?) {
                            requestAds()
                        }

                        override fun onPolicyRequired(isRequired: Boolean) {
                        }
                    })
                } else {
                    requestAds()
                }
            }
        }
    }

    private fun requestAds() {
        activity?.let {
            consentManager?.getConsentResult(
                it
            )
        }?.let { PianoApplication.appResumeAdHelper?.setRequestAppResumeValid(it) }
        interAdSplashHelper?.requestAds(InterstitialAdParam.Request.create())
        if (isOpenLFO()) {
            activity?.let {
                if (ConfigPreferences.getInstance(it).isShowLfo1HighFloor == true) {
                    NativeUtils.requestLFO1Alternate(
                        it,
                        getString(R.string.NATIVE_GG_LANGUAGE_2F),
                        getString(R.string.NATIVE_GG_LANGUAGE)
                    ) {
                        ConfigPreferences.getInstance(it).isShowNativeLanguage == true
                    }
                } else {
                    NativeUtils.requestNativeLFO1(it, getString(R.string.NATIVE_GG_LANGUAGE)) {
                        ConfigPreferences.getInstance(it).isShowNativeLanguage == true
                    }
                }
            }
        } else if (isOpenOnboarding()) {

            activity?.let {
                NativeUtils.requestNativeOnboarding1(it, getString(R.string.NATIVE_GG_ONBOARDING)) {
                    ConfigPreferences.getInstance(it).isShowNativeOnboarding == true
                }
            }
        }
    }

    private fun isOpenLFO(): Boolean {
        val isLanguageConfig = context?.let {
            SharedPreferencesManager.getInstance(it).getString("selected_language").trim()
                .isEmpty()
        } ?: false
        return isLanguageConfig
    }

    private fun isOpenOnboarding(): Boolean {
        val isViewOnboardingComplete = context?.let {
            SharedPreferencesManager.getInstance(it).getBoolean("view_onboarding_complete")
        } ?: false
        return !isViewOnboardingComplete
    }

    private fun handleOpenScreens(openLanguage: Boolean, openIntro: Boolean) {
        if (openLanguage) {
            val action = SplashFragmentDirections.actionSplashToLFO()
            findNavControllerSafely(R.id.splashFragment)?.navigate(action)
        } else {
            if (openIntro) {
                activity?.let {
                    NativeUtils.requestNativeOnboarding2(it, getString(R.string.NATIVE_GG_ONBOARDING)) {
                        true
                    }
                }
                val action = SplashFragmentDirections.actionSplashFragmentToIntroFragment()
                action.arguments.putString("fromScreen", "Splash")
                findNavControllerSafely(R.id.splashFragment)?.navigate(action)
            } else {
                val action =
                    if (checkPermission(requireContext())) SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                    else SplashFragmentDirections.actionSplashFragmentToPermissionFragment()
                findNavController().navigate(action)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        binding = FragmentSplashBinding.bind(view)
        viewModel = ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]

        AnalyticsUtil.logEvent(AnalyticsUtil.SPLASH_OPEN)
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())

        handleBack()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interAdSplashHelper?.unregisterAllAdListener()
        PianoApplication.appResumeAdHelper?.setEnableAppResumeOnScreen()
//        MainActivity.isShowVideoAd = false
    }

    private fun handleBack() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitProcess(0)
                }
            })
    }

    companion object {
        var isLive = false;

    }

    override fun onDestroy() {
        super.onDestroy()
        isLive = false
    }

}