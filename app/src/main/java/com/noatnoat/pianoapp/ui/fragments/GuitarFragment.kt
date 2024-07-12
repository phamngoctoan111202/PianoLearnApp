package com.noatnoat.pianoapp.ui.fragments

import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentGuitarBinding
import com.noatnoat.pianoapp.ui.base.BaseFragment

class GuitarFragment : BaseFragment() {
    private var _binding: FragmentGuitarBinding? = null
    private val binding get() = _binding!!
    private lateinit var soundPool: SoundPool
    private val soundIds = hashMapOf<Int, Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGuitarBinding.inflate(inflater, container, false)
        initializeSoundPool()
        setupButtonSounds()
        setFragmentLandscapeOrientation()
        return binding.root

    }

    private fun initializeSoundPool() {
        soundPool = SoundPool.Builder().setMaxStreams(6).build()
        // Load sound files into SoundPool
        soundIds[R.id.button_e_low] = soundPool.load(context, R.raw.a1_2, 1)
        soundIds[R.id.button_a] = soundPool.load(context, R.raw.am_3, 1)
        soundIds[R.id.button_d] = soundPool.load(context, R.raw.am_4, 1)
        soundIds[R.id.button_g] = soundPool.load(context, R.raw.am_5, 1)
        soundIds[R.id.button_b] = soundPool.load(context, R.raw.am_3, 1)
        soundIds[R.id.button_e_high] = soundPool.load(context, R.raw.a1_2, 1)
    }

    private fun setupButtonSounds() {
        val buttonIds = listOf(R.id.button_e_low, R.id.button_a, R.id.button_d, R.id.button_g, R.id.button_b, R.id.button_e_high)
        buttonIds.forEach { buttonId ->
            val view = binding.root.findViewById<View>(buttonId)
            if (view is ImageView) {
                view.setOnClickListener {
                    soundIds[buttonId]?.let { soundId ->
                        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                    }
                }
            } else if (view is Button) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool.release()
        setFragmentPortraitOrientation()
        _binding = null

    }
}