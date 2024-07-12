package com.noatnoat.pianoapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentPianoStylesBinding
import com.noatnoat.pianoapp.ui.adapters.StylesPagerAdapter
import com.noatnoat.pianoapp.ui.views.ZoomOutPageTransformer

class PianoStylesFragment : Fragment() {
    private var _binding: FragmentPianoStylesBinding? = null
    private val binding get() = _binding!!

    private lateinit var stylesPagerAdapter: StylesPagerAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPianoStylesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val styles = listOf(R.drawable.img_piano_style_1, R.drawable.img_piano_style_2, R.drawable.img_piano_style_3)
        stylesPagerAdapter = StylesPagerAdapter(styles, requireContext())
        binding.viewPager.adapter = stylesPagerAdapter

        binding.viewPager.clipToPadding = false
        binding.viewPager.setPadding(100, 0, 100, 0) // Đặt padding cho ViewPager
        binding.viewPager.pageMargin = 10

        binding.viewPager.setPageTransformer(true, ZoomOutPageTransformer())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}