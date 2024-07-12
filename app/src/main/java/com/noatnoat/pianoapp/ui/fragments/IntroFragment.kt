package com.noatnoat.pianoapp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.noatnoat.pianoapp.utils.AnalyticsUtil
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.SharedPreferencesManager
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdConfig
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdHelper
import com.noatnoat.pianoapp.admob.helper.adnative.params.NativeAdParam
import com.noatnoat.pianoapp.admob.listener.NativeAdCallback
import com.noatnoat.pianoapp.checkPermission
import com.noatnoat.pianoapp.databinding.FragmentIntroBinding
import com.noatnoat.pianoapp.databinding.ItemIntroBinding
import com.noatnoat.pianoapp.admob.lfo.NativeUtils
import com.noatnoat.pianoapp.ui.base.BaseFragment
import com.noatnoat.pianoapp.utils.Helper
import kotlin.system.exitProcess

class IntroFragment : BaseFragment() {
    private lateinit var binding: FragmentIntroBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val nativeAdHelper by lazy { initNativeAd() }
    private var isShowNativeOb1Preload = false
    private var isShowNativeOb2Preload = false
    private var isShowNativeOb3Preload = false
    private var isRequestValid = true
    private fun initNativeAd(): NativeAdHelper? {
        activity?.let { myActivity ->
            val config = NativeAdConfig(
                idAds = getString(R.string.NATIVE_GG_ONBOARDING),
                canShowAds = ConfigPreferences.getInstance(myActivity).isShowNativeOnboarding == true,
                canReloadAds = true,
                layoutId = R.layout.layout_native_medium
            )
            return NativeAdHelper(myActivity, this, config)
        }
        return null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_intro, container, false)
        binding = FragmentIntroBinding.bind(view)
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())

        handleBack()

        return view
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nativeAdHelper?.setNativeContentView(binding.flNativeAds)
        nativeAdHelper?.setShimmerLayoutView(binding.includeShimmer.shimmerContainerNative)
        nativeAdHelper?.registerAdListener(object : NativeAdCallback {
            override fun populateNativeAd() {

            }

            override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {

            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isRequestValid = true
            }

            override fun onAdClicked() {

            }

            override fun onAdImpression() {
                isRequestValid = true
            }

            override fun onAdFailedToShow(adError: AdError) {
                isRequestValid = true
            }
        })
        handleShowNative(0)
        setStatusBarColor(R.drawable.gradient_theme)
        setUpIntro()
    }

    private fun setUpIntro() {
        val introList = listOf(
            IntroContent(
                getString(R.string.intro_1),
                R.drawable.img_intro1
            ),
            IntroContent(
                getString(R.string.intro_2),
                R.drawable.img_intro2
            ),
            IntroContent(
                getString(R.string.intro_3),
                R.drawable.img_intro3
            )
        )
        var count = 1
//        lnNative = binding.lnNative

        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.4f
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnNext.isEnabled = true
            binding.btnNext.alpha = 1f
        }, 3000)

        val adapter = IntroAdapter(introList)
        binding.apply {
            viewPager.adapter = adapter
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {
                            btnNext.text = requireContext().getString(R.string.next)
                            AnalyticsUtil.logEvent(AnalyticsUtil.ONBOARDING1_NEXT_VIEW)
                        }

                        1 -> {
                            btnNext.text = requireContext().getString(R.string.next)
                            AnalyticsUtil.logEvent(AnalyticsUtil.ONBOARDING2_NEXT_VIEW)
                        }

                        2 -> {
                            btnNext.text = requireContext().getString(R.string.next)
                            AnalyticsUtil.logEvent(AnalyticsUtil.ONBOARDING3_NEXT_VIEW)
                        }
                    }
                    handleShowNative(position)
                }
            })
            indicator.attachTo(binding.viewPager)
            btnNext.setOnClickListener {
                if (!Helper.isDoubleClick()) {
                    if (viewPager.currentItem == introList.size - 1) {
                        AnalyticsUtil.logEvent(AnalyticsUtil.ONBOARDING3_NEXT_CLICK)
                        sharedPreferencesManager.saveBoolean("is_first_time", false)
                        sharedPreferencesManager.saveBoolean("view_onboarding_complete", true)

                        val action =
                            if (checkPermission(requireContext())) IntroFragmentDirections.actionIntroFragmentToHomeFragment()
                            else IntroFragmentDirections.actionIntroFragmentToPermissionFragment()
                        findNavController().navigate(action)
                    } else {
                        if (viewPager.currentItem == 0) {
                            AnalyticsUtil.logEvent(AnalyticsUtil.ONBOARDING1_NEXT_CLICK)
                        } else if (viewPager.currentItem == 1) {
                            AnalyticsUtil.logEvent(AnalyticsUtil.ONBOARDING2_NEXT_CLICK)
                        }
                        viewPager.currentItem = viewPager.currentItem + 1
                    }
                }
            }
        }
    }

    fun handleShowNative(pagePosition: Int) {
        if (isShowNativeOb1Preload && isShowNativeOb2Preload && isShowNativeOb3Preload) {
            if (isRequestValid) {
                isRequestValid = false
                nativeAdHelper?.requestAds(NativeAdParam.Request)
            }
        } else {
            NativeUtils.nativeOb1.observe(viewLifecycleOwner) { appNativeAd ->
                if (appNativeAd != null && pagePosition == 0 && !isShowNativeOb1Preload) {
                    isShowNativeOb1Preload = true
                    nativeAdHelper?.requestAds(NativeAdParam.Ready(appNativeAd.nativeAd))
                }
            }
            NativeUtils.nativeOb2.observe(viewLifecycleOwner) { appNativeAd ->
                if (appNativeAd != null && pagePosition == 1 && !isShowNativeOb2Preload) {
                    isShowNativeOb2Preload = true
                    nativeAdHelper?.requestAds(NativeAdParam.Ready(appNativeAd.nativeAd))
                }
            }
            NativeUtils.nativeOb3.observe(viewLifecycleOwner) { appNativeAd ->
                if (appNativeAd != null && pagePosition == 2 && !isShowNativeOb3Preload) {
                    isShowNativeOb3Preload = true
                    nativeAdHelper?.requestAds(NativeAdParam.Ready(appNativeAd.nativeAd))
                }
            }

        }
    }

    data class IntroContent(val intro: String, val image: Int)

    inner class IntroAdapter(private val introList: List<IntroContent>) :
        RecyclerView.Adapter<IntroAdapter.IntroViewHolder>() {
        inner class IntroViewHolder(private val binding: ItemIntroBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(introContent: IntroContent) {
                binding.txtIntro.text = introContent.intro
                binding.imgIntro.setImageResource(introContent.image)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
            val binding =
                ItemIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return IntroViewHolder(binding)
        }

        override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
            holder.bind(introList[position])
        }

        override fun getItemCount(): Int {
            return introList.size
        }
    }
}