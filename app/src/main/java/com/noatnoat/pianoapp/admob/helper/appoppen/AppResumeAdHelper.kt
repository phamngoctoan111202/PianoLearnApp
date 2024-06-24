package com.noatnoat.pianoapp.admob.helper.appoppen

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.noatnoat.pianoapp.admob.AdmobManager
import com.noatnoat.pianoapp.admob.data.ContentAd
import com.noatnoat.pianoapp.admob.dialog.LoadingAdsDialog
import com.noatnoat.pianoapp.admob.listener.AppOpenAdCallBack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by ViO on 16/03/2024.
 */

class AppResumeAdHelper(
    application: Application,
    private val lifecycleOwner: LifecycleOwner,
    private val config: AppResumeAdConfig
) : LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private var appOpenAdManager: AppOpenAdManager
    private var isActivityInValid = false
    private var isDisableAppResumeOnScreen = false
    private var isDisableAppResumeByClickAction = false
    private var dialogLoading: LoadingAdsDialog? = null

    private val listAdCallback: CopyOnWriteArrayList<AppOpenAdCallBack> = CopyOnWriteArrayList()
    private val TAG= AppResumeAdHelper::class.simpleName

    private var isRequestAppResumeValid = true

    fun setRequestAppResumeValid(isValid : Boolean){
        isRequestAppResumeValid = isValid
    }
    fun setDisableAppResumeByClickAction() {
        isDisableAppResumeByClickAction = true
    }

    fun setDisableAppResumeOnScreen() {
        isDisableAppResumeOnScreen = true
    }

    fun setEnableAppResumeOnScreen() {
        isDisableAppResumeOnScreen = false
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()
        appOpenAdManager.setAdUnitId(config.idAds)
        appOpenAdManager.loadAd(application)
        application.registerActivityLifecycleCallbacks(this)
    }


    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        if (!isRequestAppResumeValid) return
        config.listClassInValid.forEach { classItem ->
            if (classItem.simpleName == activity::class.simpleName) {
                isActivityInValid = true
                return@forEach
            } else {
                isActivityInValid = false
            }
        }
    }


    override fun onActivityStarted(activity: Activity) {
        if (!isRequestAppResumeValid) return
        Log.e(TAG, "onActivityStarted: ${activity::class.simpleName}", )
        if (isDisableAppResumeByClickAction) {
            isDisableAppResumeByClickAction = false
            return
        }
        config.listClassInValid.forEach { classItem ->
            if (classItem.simpleName == activity::class.simpleName) {
                isActivityInValid = true
                return@forEach
            } else {
                isActivityInValid = false
            }
        }
        if (AdmobManager.isAdsClicked && !isActivityInValid) {
            AdmobManager.adsClickedInValid()
            return
        }
        if (isActivityInValid || isDisableAppResumeOnScreen || AdmobManager.isShowAdsFullScreen || AdmobManager.isAdsClicked) return
//        if (!MainActivity.isShowVideoAd && !MainActivity.isShowInter) handleShowAppOpenResume(activity)
            handleShowAppOpenResume(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }

    private fun showDialogLoading(activity: Activity) {
        if (dialogLoading == null) {
            dialogLoading = LoadingAdsDialog(activity)
        }
        dialogLoading?.show()
    }

    private fun dismissLoading() {
        try {
            dialogLoading?.dismiss()
            dialogLoading = null
        } catch (ex: Exception) {
            dialogLoading = null
            ex.printStackTrace()
        }
    }

    private fun handleShowAppOpenResume(activity: Activity) {
        if (!isRequestAppResumeValid) return
        if (appOpenAdManager.isAdAvailable()){
            lifecycleOwner.lifecycleScope.launch {
                showDialogLoading(activity)
                delay(800)
                appOpenAdManager.showAdIfAvailable(activity, invokeListenerAdCallback())
            }
        }
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        if (!isRequestAppResumeValid) return
        config.listClassInValid.forEach { classItem ->
            if (classItem.simpleName == activity::class.simpleName) {
                isActivityInValid = true
                return@forEach
            } else {
                isActivityInValid = false
            }
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }


    fun registerAdListener(adCallback: AppOpenAdCallBack) {
        this.listAdCallback.add(adCallback)
    }

    fun unregisterAdListener(adCallback: AppOpenAdCallBack) {
        this.listAdCallback.remove(adCallback)
    }

    fun unregisterAllAdListener() {
        this.listAdCallback.clear()
    }

    private fun invokeAdListener(action: (adCallback: AppOpenAdCallBack) -> Unit) {
        listAdCallback.forEach(action)
    }

    private fun invokeListenerAdCallback(): AppOpenAdCallBack {
        return object : AppOpenAdCallBack {
            override fun onAppOpenAdShow() {
                invokeAdListener { it.onAppOpenAdShow() }
            }

            override fun onAppOpenAdClose() {
                dismissLoading()
                invokeAdListener { it.onAppOpenAdClose() }
            }

            override fun onAdLoaded(data: ContentAd.AdmobAd.ApAppOpenAd) {
                invokeAdListener { it.onAdLoaded(data) }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                dismissLoading()
                invokeAdListener { it.onAdFailedToLoad(loadAdError) }
            }

            override fun onAdClicked() {
                invokeAdListener { it.onAdClicked() }
            }

            override fun onAdImpression() {
                invokeAdListener { it.onAdImpression() }
            }

            override fun onAdFailedToShow(adError: AdError) {
                dismissLoading()
                invokeAdListener { it.onAdFailedToShow(adError) }
            }

        }
    }
}