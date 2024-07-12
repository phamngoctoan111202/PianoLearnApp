package com.noatnoat.pianoapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noatnoat.pianoapp.ui.base.BaseFragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.noatnoat.pianoapp.databinding.FragmentInfoBinding
import com.noatnoat.pianoapp.ui.adapters.InfoPagerAdapter

class InfoFragment : BaseFragment() {
    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        setupViewPager()
        return binding.root
    }

    private fun setupViewPager() {
        val adapter = InfoPagerAdapter(childFragmentManager, lifecycle)
        binding.infoViewPager.adapter = adapter
        TabLayoutMediator(binding.infoTabLayout, binding.infoViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Piano"
                1 -> "Guitar"
                2 -> "Drum"
                3 -> "Saxophone"
                else -> null
            }
        }.attach()
    }
}