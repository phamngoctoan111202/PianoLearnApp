package com.noatnoat.pianoapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentHomeBinding
import com.noatnoat.pianoapp.ui.base.BaseFragment
import android.Manifest
import android.content.pm.PackageManager


class HomeFragment : BaseFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    companion object {
        private const val PERMISSION_REQUEST_READ_STORAGE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivPiano.setOnClickListener {
            navigateTo(R.id.action_homeFragment_to_pianoFragment)
        }

        binding.ivGuitar.setOnClickListener {
            navigateTo(R.id.action_homeFragment_to_guitarFragment)
        }

        binding.ivDrumset.setOnClickListener {
            navigateTo(R.id.action_homeFragment_to_drumFragment)
        }

        binding.ivSaxophone.setOnClickListener {
            navigateTo(R.id.action_homeFragment_to_saxophoneFragment)
        }

        binding.ivRecordList.setOnClickListener {
            navigateToRecordListWithPermissionCheck()
        }

        binding.moreInfo.setOnClickListener {
            navigateTo(R.id.action_homeFragment_to_infoFragment)
        }

    }


    private fun navigateToRecordListWithPermissionCheck() {
        val permissionsToRequest = mutableListOf<String>()
        val hasReadPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (!hasReadPermission) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_READ_STORAGE)
        } else {
            navigateToRecordList()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_STORAGE && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            navigateToRecordList()
        } else {
        }
    }

    private fun navigateToRecordList() {
        findNavController().navigate(R.id.action_homeFragment_to_recordFragment)
    }

    private fun navigateTo(actionId: Int) {
        findNavController().navigate(actionId)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
