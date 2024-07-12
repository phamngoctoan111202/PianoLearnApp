package com.noatnoat.pianoapp.ui.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.admob.AdmobFactory
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.helper.banner.BannerAdConfig
import com.noatnoat.pianoapp.admob.helper.banner.BannerAdHelper
import com.noatnoat.pianoapp.admob.helper.banner.params.BannerAdParam
import com.noatnoat.pianoapp.admob.listener.BannerAdCallBack
import com.noatnoat.pianoapp.admob.listener.NativeAdCallback
import com.noatnoat.pianoapp.databinding.ActivityMainBinding
import com.noatnoat.pianoapp.admob.lfo.NativeUtils
import com.noatnoat.pianoapp.ui.base.BaseActivity
import com.noatnoat.pianoapp.utils.Helper
import dagger.hilt.android.AndroidEntryPoint
import dev.b3nedikt.app_locale.AppLocale

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding




    private val appCompatDelegate: AppCompatDelegate by lazy {
        ViewPumpAppCompatDelegate(
            baseDelegate = super.getDelegate(),
            baseContext = this,
            wrapContext = AppLocale::wrap
        )
    }

    override fun getDelegate(): AppCompatDelegate {
        return appCompatDelegate
    }

    private val bannerAdHelper by lazy { initBannerAd() }

    private fun initBannerAd(): BannerAdHelper? {
        this@MainActivity.let { myActivity ->
            val config = BannerAdConfig(
                idAds = getString(R.string.BANNER_GG),
                canShowAds = ConfigPreferences.getInstance(myActivity).isShowBanner == true,
                canReloadAds = true,
            )
            return BannerAdHelper(activity = myActivity, lifecycleOwner = this, config = config)
        }
        return null
    }

    var isRequestBannerAdValid = true
    private fun requestBannerAds() {
        bannerAdHelper?.setBannerContentView(binding.frAds)
//        bannerAdHelper?.requestAds(BannerAdParam.Request)
        bannerAdHelper?.registerAdListener(bannerAdCallBack)
        isRequestBannerAdValid = true
    }

    private val bannerAdCallBack = object : BannerAdCallBack {
        override fun onAdClicked() {
            Log.e("TAG", "onAdClicked: ")
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            Log.e("TAG", "onAdFailedToLoad: ")
            isRequestBannerAdValid = true
        }

        override fun onAdFailedToShow(adError: AdError) {
            Log.e("TAG", "onAdFailedToShow: ")
        }

        override fun onAdImpression() {
            Log.e("TAG", "onAdImpression: ")
        }

        override fun onAdLoaded(data: ContentAd.AdmobAd.ApBannerAd) {
            Log.e("TAG", "onAdLoaded: ")
            isRequestBannerAdValid = true
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        try {
            if (hasFocus) decorView?.setSystemUiVisibility(hideSystemUI())
        } catch (e: java.lang.Exception) {
        }
    }

    private fun hideSystemUI(): Int {
        return (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private var decorView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = Color.WHITE
        setUpNavController()
        decorView = window.decorView;

        initViewExitApp()




        this@MainActivity.let {
            NativeUtils.requestNativeExit(it, getString(R.string.NATIVE_GG_EXIT)) {
                true
            }
        }

        // request banner
        requestBannerAds()
    }

    var lnExitApp: LinearLayout? = null
    var tvYes: TextView? = null
    var tvCancel: TextView? = null

    private fun initViewExitApp() {
        try {
            lnExitApp = findViewById(R.id.lnDialogExitApp)
            tvYes = findViewById(R.id.tvYesExitApp)
            tvCancel = findViewById(R.id.tvCancelExitApp)
            lnExitApp!!.visibility = View.GONE
            lnExitApp!!.setOnClickListener {
                lnExitApp!!.visibility = View.GONE
            }
            tvCancel!!.setOnClickListener(View.OnClickListener {
                lnExitApp!!.visibility = View.GONE
            })
            tvYes!!.setOnClickListener {
                lnExitApp!!.visibility = View.GONE
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    override fun onBackPressed() {
////        if (HomeFragment222.isShowHome) {
////            binding.lnDialogExitApp.isVisible = binding.lnDialogExitApp.visibility != View.VISIBLE
////            initNativeExit()
////        } else {
////            super.onBackPressed()
////        }
//    }

    private fun initNativeExit() {
        if (binding.layoutAdNative.isVisible) {
            NativeUtils.nativeExit.observe(this) {
                if (it != null) {
                    AdmobFactory.getInstance().populateNativeAdView(
                        this,
                        it.nativeAd,
                        R.layout.layout_native_big,
                        binding.layoutAdNative,
                        binding.includeShimmer.shimmerContainerNative,
                        object : NativeAdCallback {
                            override fun populateNativeAd() {

                            }

                            override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {

                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                            }

                            override fun onAdClicked() {

                            }

                            override fun onAdImpression() {

                            }

                            override fun onAdFailedToShow(adError: AdError) {
                                binding.layoutAdNative.isVisible = false
                            }

                        })
                } else {
                    binding.layoutAdNative.isVisible = false
                }
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
        private const val MANAGE_STORAGE_REQUEST_CODE = 102
    }


    private fun setUpNavController() {
        binding.apply {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            var lnNativeContainer: FrameLayout = binding.frAdsContainer
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.homeFragment) {
                    if (isRequestBannerAdValid) {
                        bannerAdHelper?.requestAds(BannerAdParam.Request)
                        isRequestBannerAdValid = false
                        Helper.myLog("RELOAD BANNER")
                    }
                    lnNativeContainer.visibility = View.VISIBLE
                } else {
                    lnNativeContainer.visibility = View.GONE
                }
                if (destination.id == R.id.homeFragment || destination.id == R.id.introFragment) {
                    window.navigationBarColor = Color.WHITE
                } else {
                    window.navigationBarColor = Color.parseColor("#F7F7F7")
                }
                if (destination.id == R.id.homeFragment) {
                    window.statusBarColor = Color.parseColor("#1A998E")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        window.insetsController?.setSystemBarsAppearance(
                            0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        window.decorView.systemUiVisibility = 0
                    }
                } else {
                    window.statusBarColor = Color.WHITE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        window.insetsController?.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                }

            }
        }
    }




    private fun navigateTo(actionId: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(actionId)
    }



    override fun onResume() {
        super.onResume()
    }

}