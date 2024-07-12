package com.noatnoat.pianoapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.noatnoat.pianoapp.ui.fragments.FragmentInfoDrumset
import com.noatnoat.pianoapp.ui.fragments.FragmentInfoGuitar
import com.noatnoat.pianoapp.ui.fragments.FragmentInfoPiano
import com.noatnoat.pianoapp.ui.fragments.FragmentInfoSaxophone


class InfoPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = 4
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentInfoPiano()
            1 -> FragmentInfoGuitar()
            2 -> FragmentInfoDrumset()
            3 -> FragmentInfoSaxophone()
            else -> Fragment()
        }
    }


}