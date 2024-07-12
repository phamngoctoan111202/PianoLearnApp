package com.noatnoat.pianoapp.ui.fragments

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import com.noatnoat.pianoapp.databinding.FragmentPermissionBinding
import androidx.navigation.fragment.findNavController
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.admob.ConfigPreferences
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdConfig
import com.noatnoat.pianoapp.admob.helper.adnative.NativeAdHelper
import com.noatnoat.pianoapp.admob.helper.adnative.params.NativeAdParam
import com.noatnoat.pianoapp.ui.base.BaseFragment
import com.noatnoat.pianoapp.utils.Helper
import kotlin.system.exitProcess
import android.Manifest
import android.os.Build
import androidx.core.content.ContextCompat
import com.noatnoat.pianoapp.helpers.PermissionHelper


class PermissionFragment : BaseFragment() {
    private lateinit var binding: FragmentPermissionBinding
    private lateinit var permissionHelper: PermissionHelper

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPermissionBinding.inflate(inflater, container, false)
        handleBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionHelper = PermissionHelper(this, requireContext())

        nativeAdHelper?.setNativeContentView(binding.flNativeAds)
        nativeAdHelper?.setShimmerLayoutView(binding.includeShimmer.shimmerContainerNative)
        nativeAdHelper?.requestAds(NativeAdParam.Request.create())
        setStatusBarColor(R.drawable.gradient_theme)

        binding.apply {
            btnContinue.setOnClickListener {
                if (!Helper.isDoubleClick()) {
                    val action = PermissionFragmentDirections.actionPermissionFragmentToHomeFragment()
                    findNavController().navigate(action)
                }
            }
        }

        val switchAccessMusic = view.findViewById<SwitchCompat>(R.id.switchAudioFiles)
        val switchRecordAudio = view.findViewById<SwitchCompat>(R.id.switchMicrophone)

        switchAccessMusic.isChecked = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        switchRecordAudio.isChecked = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        switchAccessMusic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                permissionHelper.requestPermission(PermissionHelper.PERMISSION_REQUEST_ACCESS_MUSIC)
            }
        }

        switchRecordAudio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                permissionHelper.requestPermission(PermissionHelper.PERMISSION_REQUEST_RECORD_AUDIO)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults, binding.switchAudioFiles, binding.switchMicrophone)
    }

    private fun handleBack() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitProcess(0)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun checkContinue() {
        binding.apply {
            btnContinue.setTextColor(Color.parseColor("#1A998E"))
        }
    }

    override fun onResume() {
        super.onResume()
        checkContinue()
    }
}