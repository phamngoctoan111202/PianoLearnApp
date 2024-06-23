package com.noatnoat.pianoapp.splash

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.noatnoat.pianoapp.databinding.FragmentPermissionBinding
import androidx.navigation.fragment.findNavController
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdConfig
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdHelper
import com.noatnoat.pianoapp.admob.helper.adnative.params.NativeAdParam
import com.noatnoat.pianoapp.utils.Helper
import kotlin.system.exitProcess

class PermissionFragment : Fragment() {
    private lateinit var binding: FragmentPermissionBinding

    private val nativeAdHelper by lazy { initNativeAd() }
    private fun initNativeAd(): NativeAdHelper? {
        activity?.let { myActivity ->
            val config = NativeAdConfig(
                idAds = getString(R.string.NATIVE_GG_PERMISSION),
                canShowAds = ConfigPreferences.getInstance(myActivity).isShowNativePermission == true,
                canReloadAds = true,
                layoutId = R.layout.layout_native_medium
            )
            return NativeAdHelper(myActivity, this, config)
        }
        return null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPermissionBinding.inflate(inflater, container, false)

        handleBack()
        return binding.root
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

    override fun onDestroy() {
        super.onDestroy()
//        MainActivity.Companion.isShowInter = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nativeAdHelper?.setNativeContentView(binding.flNativeAds)
        nativeAdHelper?.setShimmerLayoutView(binding.includeShimmer.shimmerContainerNative)
        nativeAdHelper?.requestAds(NativeAdParam.Request.create())

        binding.apply {

//            MainActivity.Companion.isShowInter = true

            btnContinue.setOnClickListener {
                if (!Helper.isDoubleClick()) {
                    val action =
                        PermissionFragmentDirections.actionPermissionFragmentToHomeFragment()
                    findNavController().navigate(action)
                }
            }

        }
    }

    private fun checkContinue() {
        binding.apply {
//            if (switchReminder.isChecked && switchNotification.isChecked) {
            btnContinue.setTextColor(Color.parseColor("#1A998E"))
//            } else {
//                btnContinue.setTextColor(Color.parseColor("#8C8C92"))
//            }
        }
    }

    override fun onResume() {
        super.onResume()

        checkContinue()
    }


}