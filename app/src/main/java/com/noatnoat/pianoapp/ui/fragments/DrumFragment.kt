package com.noatnoat.pianoapp.ui.fragments
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentDrumBinding
import com.noatnoat.pianoapp.ui.base.BaseFragment

class DrumFragment : BaseFragment() {
    private var _binding: FragmentDrumBinding? = null
    private val binding get() = _binding!!
    private lateinit var soundPool: SoundPool
    private val soundIds = hashMapOf<Int, Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_drum, container, false)
        initializeSoundPool()
        setupButtonSounds()
        setFragmentLandscapeOrientation()
        return binding.root
    }

    private fun initializeSoundPool() {
        soundPool = SoundPool.Builder().setMaxStreams(6).build()
        soundIds[R.id.drum_1_1] = soundPool.load(context, R.raw.sound_fus1, 1)
        soundIds[R.id.drum_1_2] = soundPool.load(context, R.raw.sound_fus2, 1)
        soundIds[R.id.drum_2_1] = soundPool.load(context, R.raw.sound_fus1, 1)
        soundIds[R.id.drum_2_2] = soundPool.load(context, R.raw.sound_fus1, 1)
        soundIds[R.id.drum_3_1] = soundPool.load(context, R.raw.sound_fus2, 1)
        soundIds[R.id.drum_3_2] = soundPool.load(context, R.raw.sound_fus2, 1)
        soundIds[R.id.drum_4_1] = soundPool.load(context, R.raw.sound_fus2, 1)
        soundIds[R.id.drum_4_2] = soundPool.load(context, R.raw.sound_fus2, 1)
        soundIds[R.id.drum_5_1] = soundPool.load(context, R.raw.sound_fus2, 1)
        soundIds[R.id.drum_5_2] = soundPool.load(context, R.raw.sound_fus2, 1)
        soundIds[R.id.drum_bass_1_1] = soundPool.load(context, R.raw.sound_tom3_2, 1)
        soundIds[R.id.drum_bass_1_2] = soundPool.load(context, R.raw.sound_tom4_2, 1)
        soundIds[R.id.drum_bass_2_1] = soundPool.load(context, R.raw.sound_tom5_2, 1)
        soundIds[R.id.drum_bass_2_2] = soundPool.load(context, R.raw.sound_crash2, 1)
        soundIds[R.id.drum_bass_3_1] = soundPool.load(context, R.raw.sound_crash2_2, 1)
    }

    private fun setupButtonSounds() {
        soundIds.keys.forEach { buttonId ->
            binding.root.findViewById<View>(buttonId)?.setOnClickListener {
                soundIds[buttonId]?.let { soundId ->
                    soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                    Log.d("DrumFragment", "Playing sound with id $soundId")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setFragmentPortraitOrientation()
        soundPool.release()
        _binding = null
    }
}
