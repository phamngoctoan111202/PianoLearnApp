package com.noatnoat.pianoapp.admob.cmp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import com.noatnoat.pianoapp.admob.cmp.interfaces.OnConsentResponse
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class ConsentManager private constructor(private val activity: Activity) {
    companion object {
        @JvmStatic
        fun getInstance(activity: Activity): ConsentManager = ConsentManager(activity)
    }

    private val consentInformation by lazy { UserMessagingPlatform.getConsentInformation(activity) }
    private var onConsentResponse: OnConsentResponse? = null

    val canRequestAds: Boolean get() = consentInformation.canRequestAds()

    fun getConsentResult(context: Context): Boolean {
        val sharedPref =
            context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        val purposeConsents = sharedPref.getString("IABTCF_PurposeConsents", "")
        Log.d(
            "ConsentManager",
            "consentResult: $purposeConsents"
        )
        // Purposes are zero-indexed. Index 0 contains information about Purpose 1.
        if (purposeConsents!!.isNotEmpty()) {
            val purposeOneString = purposeConsents[0].toString()
            return purposeOneString == "1"
        }
        return true
    }

    /**
     * @param deviceId: You can provide device id via parameter,
     *          if you want or it will get from the following function, if no id has been provided.
     *
     * @param onConsentResponse: 'onResponse' used to call ads, it will be called in either success or error case
     *                          'onPolicyRequired' will indicate whether to show privacy button or not
     */

    fun initDebugConsent(deviceId: String = getDeviceId(), onConsentResponse: OnConsentResponse) {
        this.onConsentResponse = onConsentResponse
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId(deviceId)
            .build()

        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .build()

        // Resetting to show everytime in debug mode for testing
        consentInformation.reset()
        requestConsent(params)
    }

    fun initReleaseConsent(onConsentResponse: OnConsentResponse) {
        this.onConsentResponse = onConsentResponse
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        requestConsent(params)
    }

    private fun requestConsent(params: ConsentRequestParameters) {
        consentInformation.requestConsentInfoUpdate(activity, params, {
            // The consent information state was updated.
            if (consentInformation.isConsentFormAvailable && consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                Log.i("ConsentManager", "initConsent: Available & Required")
                loadForm()
            } else {
                Log.i("ConsentManager", "initConsent: Neither Available nor Required")
                onConsentResponse?.onResponse()
            }
        }, { error ->
            // Handle the error.
            Log.e("ConsentManager", "requestConsent: $error")
            onConsentResponse?.onResponse(error.message)
        })
    }

    private fun loadForm() {
        // Loads a consent form. Must be called on the main thread.
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
            formError?.let {
                onConsentResponse?.onResponse(it.message)
            } ?: run {
                onConsentResponse?.onResponse()
                checkForPrivacyOptions()
            }
        }
    }

    private fun checkForPrivacyOptions() {
        val isRequired =
            consentInformation?.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        onConsentResponse?.onPolicyRequired(isRequired)
    }

    fun launchPrivacyForm(callback: (formError: FormError?) -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            formError?.let {
                Log.e("ConsentManager", "launchPrivacyForm, Error: ${formError.message}")
            } ?: kotlin.run {
                Log.d("ConsentManager", "launchPrivacyForm, Result: Shown")
            }
        }
    }

    /* ------------------------------------------------- Helpers ------------------------------------------------- */

    /**
     *  Note: Use this function only for debugging purpose, as it's not recommended
     */
    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        return try {
            val androidId =
                Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
            val digest = MessageDigest.getInstance("MD5")
            digest.update(androidId.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                java.lang.String.format("%02X", 0xFF and messageDigest[i].toInt())
            )
            hexString.toString().uppercase()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            ""
        }
    }
}