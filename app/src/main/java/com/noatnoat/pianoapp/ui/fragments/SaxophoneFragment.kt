package com.noatnoat.pianoapp.ui.fragments
import android.graphics.Color
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentSaxophoneBinding
import com.noatnoat.pianoapp.ui.activities.MainActivity
import com.noatnoat.pianoapp.ui.base.BaseFragment

class SaxophoneFragment : BaseFragment() {

    private var _binding: FragmentSaxophoneBinding? = null
    private val binding get() = _binding!!
    private lateinit var soundPool: SoundPool
    private val soundIds = hashMapOf<Int, Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saxophone, container, false)
        val view = binding.root

        initializeSoundPool()
        setupButtonSounds()

        return view
    }


    private fun initializeSoundPool() {
        soundPool = SoundPool.Builder().setMaxStreams(11).build()
        soundIds[R.id.btn_saxophone_1] = soundPool.load(context, R.raw.saxo_01, 1)
        soundIds[R.id.btn_saxophone_2] = soundPool.load(context, R.raw.saxo_02, 1)
        soundIds[R.id.btn_saxophone_3] = soundPool.load(context, R.raw.saxo_03, 1)
        soundIds[R.id.btn_saxophone_4] = soundPool.load(context, R.raw.saxo_04, 1)
        soundIds[R.id.btn_saxophone_5] = soundPool.load(context, R.raw.saxo_05, 1)
        soundIds[R.id.btn_saxophone_6] = soundPool.load(context, R.raw.saxo_06, 1)
        soundIds[R.id.btn_saxophone_7] = soundPool.load(context, R.raw.saxo_07, 1)
        soundIds[R.id.btn_saxophone_8] = soundPool.load(context, R.raw.saxo_08, 1)
        soundIds[R.id.btn_saxophone_9] = soundPool.load(context, R.raw.saxo_09, 1)
        soundIds[R.id.btn_saxophone_10] = soundPool.load(context, R.raw.saxo_10, 1)
        soundIds[R.id.btn_saxophone_11] = soundPool.load(context, R.raw.saxo_11, 1)
    }

    private fun setupButtonSounds() {
        val buttonIds = listOf(
            R.id.btn_saxophone_1, R.id.btn_saxophone_2, R.id.btn_saxophone_3,
            R.id.btn_saxophone_4, R.id.btn_saxophone_5, R.id.btn_saxophone_6,
            R.id.btn_saxophone_7, R.id.btn_saxophone_8, R.id.btn_saxophone_9,
            R.id.btn_saxophone_10, R.id.btn_saxophone_11
        )
        buttonIds.forEach { buttonId ->
            binding.root.findViewById<View>(buttonId)?.setOnClickListener {
                soundIds[buttonId]?.let { soundId ->
                    soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool.release()
        _binding = null
    }
}