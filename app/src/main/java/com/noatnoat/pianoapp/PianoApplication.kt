package com.noatnoat.pianoapp

import android.app.Activity
import com.araujo.jordan.excuseme.view.InvisibleActivity
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdActivity
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.noatnoat.pianoapp.ui.fragments.SplashFragment
import com.noatnoat.pianoapp.utils.KeyRemoteConfigDefault
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.admob.AdmobFactory
import com.noatnoat.pianoapp.admob.config.NetworkProvider
import com.noatnoat.pianoapp.admob.config.VioAdConfig
import com.noatnoat.pianoapp.admob.helper.appoppen.AppResumeAdConfig
import com.noatnoat.pianoapp.admob.helper.appoppen.AppResumeAdHelper
import com.noatnoat.pianoapp.ui.fragments.PermissionFragment
import com.noatnoat.pianoapp.utils.KeyRemoteConfigDefault.CONFIG_INTER_SPLASH
import com.noatnoat.pianoapp.utils.KeyRemoteConfigDefault.ENABLE_UMP
import com.noatnoat.pianoapp.utils.SettingConfigs
import dev.b3nedikt.app_locale.AppLocale
import dev.b3nedikt.reword.RewordInterceptor
import dev.b3nedikt.viewpump.ViewPump
import java.util.Locale

@HiltAndroidApp
class PianoApplication : MultiDexApplication() {

    private var activeActivity: Activity? = null



    override fun getResources(): Resources {
        return AppLocale.wrapResources(applicationContext, super.getResources())

    }

    companion object {
        var context: Context? = null
        var appResumeAdHelper: AppResumeAdHelper? = null
            private set
        val fetchRemoteConfigComplete: MutableLiveData<Boolean> = MutableLiveData()
    }
    private fun initAppOpenAd(): AppResumeAdHelper {
        val listClassInValid = mutableListOf<Class<*>>()
        listClassInValid.add(AdActivity::class.java)
        listClassInValid.add(SplashFragment::class.java)
        listClassInValid.add(PermissionFragment::class.java)
        listClassInValid.add(InvisibleActivity::class.java)
        val config = AppResumeAdConfig(
            idAds = getString(R.string.INTER_APP_OPEN),
            listClassInValid = listClassInValid
        )
        return AppResumeAdHelper(
            application = this,
            lifecycleOwner = ProcessLifecycleOwner.get(),
            config = config
        )
    }

    override fun onCreate() {
        super.onCreate()


        try {
            val appToken = getString(R.string.ADJUST_APP_TOKEN)
            var environment = AdjustConfig.ENVIRONMENT_PRODUCTION
            var config = AdjustConfig(this, appToken, environment)
            Adjust.onCreate(config)
        } catch (e: Exception) {
        }

        fetchRemoteConfigComplete.value = false
        FirebaseApp.initializeApp(this)
        configAds()
        appResumeAdHelper = initAppOpenAd()


        context = this
        val locales: List<Locale> = listOf(
            Locale.forLanguageTag("en"), // English
            Locale.forLanguageTag("es"), // Spanish
            Locale.forLanguageTag("pt"), // Portuguese
            Locale.forLanguageTag("hi"), // Hindi
            Locale.forLanguageTag("ko"), // Korean
            Locale.forLanguageTag("ja"), // Japanese
            Locale.forLanguageTag("de"), // German
            Locale.forLanguageTag("fr"), // French
            Locale.forLanguageTag("it"), // Italian
            Locale.forLanguageTag("in")  // Indonesian
        )
        AppLocale.supportedLocales = locales
        ViewPump.init(RewordInterceptor)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                activeActivity = activity
                Adjust.onResume()
            }

            override fun onActivityPaused(activity: Activity) {
                Adjust.onPause()
            }

            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })

        try {
            val config = FirebaseRemoteConfig.getInstance()
            SettingConfigs.getInstance().setConfig(config)
            val settings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0).build()

            config.setConfigSettingsAsync(settings)
            config.setDefaultsAsync(R.xml.remote_config_defaults)

            config.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        getConfigShowAds()
                    }
                    fetchRemoteConfigComplete.postValue(true)
                }
        } catch (ex: Exception) {
            fetchRemoteConfigComplete.postValue(true)
        }
    }

    private fun configAds() {
        val vioAdConfig = VioAdConfig.Builder()
            .buildVariantProduce(false)
            .mediationProvider(NetworkProvider.ADMOB)
            .listTestDevices(ArrayList())
            .build()
        AdmobFactory.getInstance().initAdmob(this, vioAdConfig)
    }

    private fun getConfigShowAds() {
        try {
            ConfigPreferences.getInstance(this).isEnableUMP =
                SettingConfigs.getInstance().config.getBoolean(ENABLE_UMP)
            ConfigPreferences.getInstance(this).isShowInterSplash =
                SettingConfigs.getInstance().config.getBoolean(
                    CONFIG_INTER_SPLASH
                )

            ConfigPreferences.getInstance(this).isShowNativeLanguage =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.CONFIG_NATIVE_LANGUAGE
                )
            ConfigPreferences.getInstance(this).isShowNativeOnboarding =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.CONFIG_NATIVE_ONBOARDING
                )
            ConfigPreferences.getInstance(this).isShowNativePermission =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.CONFIG_NATIVE_PERMISSION
                )

            ConfigPreferences.getInstance(this).isShowInterHome =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.CONFIG_INTER_HOME
                )
            ConfigPreferences.getInstance(this).isShowBanner =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.CONFIG_BANNER
                )
            ConfigPreferences.getInstance(this).isShowBannerSplash =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.CONFIG_BANNER_SPLASH
                )

            ConfigPreferences.getInstance(this).isShowLfo1HighFloor =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.LFO_1_HIGH_FLOOR
                )

            ConfigPreferences.getInstance(this).isShowLfo2HighFloor =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.LFO_2_HIGH_FLOOR
                )
            ConfigPreferences.getInstance(this).isShowNativeExit =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.CONFIG_NATIVE_EXIT
                )

            ConfigPreferences.getInstance(this).isShowAppOpen =
                SettingConfigs.getInstance().config.getBoolean(
                    KeyRemoteConfigDefault.CONFIG_APPOPEN_RESUME
                )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}