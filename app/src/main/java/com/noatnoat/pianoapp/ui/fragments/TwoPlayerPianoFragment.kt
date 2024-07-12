package com.noatnoat.pianoapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.SeekBar
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.helpers.piano.PianoKeyPlayerHelper
import com.noatnoat.pianoapp.helpers.piano.SeekBarHelper
import com.noatnoat.pianoapp.models.PianoKeys
import com.noatnoat.pianoapp.ui.base.BaseFragment

class TwoPlayerPianoFragment : BaseFragment() {
    private lateinit var pianoKeyPlayerHelper1: PianoKeyPlayerHelper
    private lateinit var pianoKeyPlayerHelper2: PianoKeyPlayerHelper
    private lateinit var seekBarHelper1: SeekBarHelper
    private lateinit var seekBarHelper2: SeekBarHelper
    private lateinit var keysPlayer1: Array<Button>
    private lateinit var keysPlayer2: Array<Button>

    private val whiteKeyIds = arrayOf(
        R.id.keyA1, R.id.keyB1, R.id.keyC1, R.id.keyD1, R.id.keyE1, R.id.keyF1, R.id.keyG1,
        R.id.keyA2, R.id.keyB2, R.id.keyC2, R.id.keyD2, R.id.keyE2, R.id.keyF2, R.id.keyG2,
        R.id.keyA3, R.id.keyB3, R.id.keyC3, R.id.keyD3, R.id.keyE3, R.id.keyF3, R.id.keyG3,
        R.id.keyA4, R.id.keyB4, R.id.keyC4, R.id.keyD4, R.id.keyE4, R.id.keyF4, R.id.keyG4,
        R.id.keyA5, R.id.keyB5, R.id.keyC5, R.id.keyD5, R.id.keyE5, R.id.keyF5, R.id.keyG5,
        R.id.keyA6, R.id.keyB6, R.id.keyC6, R.id.keyD6, R.id.keyE6, R.id.keyF6, R.id.keyG6,
        R.id.keyA7, R.id.keyB7, R.id.keyC7, R.id.keyD7, R.id.keyE7, R.id.keyF7, R.id.keyG7
    )

    private val blackKeyIds = arrayOf(
        R.id.keyA1Sharp, R.id.keyC1Sharp, R.id.keyD1Sharp, R.id.keyF1Sharp, R.id.keyG1Sharp,
        R.id.keyA2Sharp, R.id.keyC2Sharp, R.id.keyD2Sharp, R.id.keyF2Sharp, R.id.keyG2Sharp,
        R.id.keyA3Sharp, R.id.keyC3Sharp, R.id.keyD3Sharp, R.id.keyF3Sharp, R.id.keyG3Sharp,
        R.id.keyA4Sharp, R.id.keyC4Sharp, R.id.keyD4Sharp, R.id.keyF4Sharp, R.id.keyG4Sharp,
        R.id.keyA5Sharp, R.id.keyC5Sharp, R.id.keyD5Sharp, R.id.keyF5Sharp, R.id.keyG5Sharp,
        R.id.keyA6Sharp, R.id.keyC6Sharp, R.id.keyD6Sharp, R.id.keyF6Sharp, R.id.keyG6Sharp,
        R.id.keyA7Sharp, R.id.keyC7Sharp, R.id.keyD7Sharp, R.id.keyF7Sharp, R.id.keyG7Sharp
    )

    private val whiteKeyIds2 = arrayOf(
        R.id.keyA1_2, R.id.keyB1_2, R.id.keyC1_2, R.id.keyD1_2, R.id.keyE1_2, R.id.keyF1_2, R.id.keyG1_2,
        R.id.keyA2_2, R.id.keyB2_2, R.id.keyC2_2, R.id.keyD2_2, R.id.keyE2_2, R.id.keyF2_2, R.id.keyG2_2,
        R.id.keyA3_2, R.id.keyB3_2, R.id.keyC3_2, R.id.keyD3_2, R.id.keyE3_2, R.id.keyF3_2, R.id.keyG3_2,
        R.id.keyA4_2, R.id.keyB4_2, R.id.keyC4_2, R.id.keyD4_2, R.id.keyE4_2, R.id.keyF4_2, R.id.keyG4_2,
        R.id.keyA5_2, R.id.keyB5_2, R.id.keyC5_2, R.id.keyD5_2, R.id.keyE5_2, R.id.keyF5_2, R.id.keyG5_2,
        R.id.keyA6_2, R.id.keyB6_2, R.id.keyC6_2, R.id.keyD6_2, R.id.keyE6_2, R.id.keyF6_2, R.id.keyG6_2,
        R.id.keyA7_2, R.id.keyB7_2, R.id.keyC7_2, R.id.keyD7_2, R.id.keyE7_2, R.id.keyF7_2, R.id.keyG7_2
    )

    private val blackKeyIds2 = arrayOf(
        R.id.keyA1Sharp_2, R.id.keyC1Sharp_2, R.id.keyD1Sharp_2, R.id.keyF1Sharp_2, R.id.keyG1Sharp_2,
        R.id.keyA2Sharp_2, R.id.keyC2Sharp_2, R.id.keyD2Sharp_2, R.id.keyF2Sharp_2, R.id.keyG2Sharp_2,
        R.id.keyA3Sharp_2, R.id.keyC3Sharp_2, R.id.keyD3Sharp_2, R.id.keyF3Sharp_2, R.id.keyG3Sharp_2,
        R.id.keyA4Sharp_2, R.id.keyC4Sharp_2, R.id.keyD4Sharp_2, R.id.keyF4Sharp_2, R.id.keyG4Sharp_2,
        R.id.keyA5Sharp_2, R.id.keyC5Sharp_2, R.id.keyD5Sharp_2, R.id.keyF5Sharp_2, R.id.keyG5Sharp_2,
        R.id.keyA6Sharp_2, R.id.keyC6Sharp_2, R.id.keyD6Sharp_2, R.id.keyF6Sharp_2, R.id.keyG6Sharp_2,
        R.id.keyA7Sharp_2, R.id.keyC7Sharp_2, R.id.keyD7Sharp_2, R.id.keyF7Sharp_2, R.id.keyG7Sharp_2
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_two_player_piano, container, false)

        setupPianoKeyPlayerHelpers()
        loadSounds()
//        initializeSeekBar(view)
        initializeKeys(view)

        return view
    }

    // Inside TwoKeyboardPianoFragment class
    private fun setupPianoKeyPlayerHelpers() {
        val pianoKeys = PianoKeys.getInstance()

        val whiteKeyIds = pianoKeys.whiteKeys.map { it.keyId }.toTypedArray()
        val blackKeyIds = pianoKeys.blackKeys.map { it.keyId }.toTypedArray()
        val whiteKeySounds = pianoKeys.whiteKeys.map { it.soundId }.toTypedArray()
        val blackKeySounds = pianoKeys.blackKeys.map { it.soundId }.toTypedArray()

        pianoKeyPlayerHelper1 = PianoKeyPlayerHelper(requireContext())
        pianoKeyPlayerHelper1.loadSounds(whiteKeySounds, blackKeySounds, whiteKeyIds, blackKeyIds)

        pianoKeyPlayerHelper2 = PianoKeyPlayerHelper(requireContext())
        pianoKeyPlayerHelper2.loadSounds(whiteKeySounds, blackKeySounds, whiteKeyIds, blackKeyIds)
    }
    private fun loadSounds() {
    }

    private fun initializeSeekBar(view: View) {
        val seekBar1 = view.findViewById<SeekBar>(R.id.seekBar1)
        val scrollView1 = view.findViewById<HorizontalScrollView>(R.id.scrollView)
        val seekBar2 = view.findViewById<SeekBar>(R.id.seekBar2)
        val scrollView2 = view.findViewById<HorizontalScrollView>(R.id.scrollView2)

        seekBar1?.let {
            seekBarHelper1 = SeekBarHelper(it, scrollView1)
            seekBarHelper1.setupSeekBar()
        }

        seekBar2?.let {
            seekBarHelper2 = SeekBarHelper(it, scrollView2)
            seekBarHelper2.setupSeekBar()
        }
    }

    private fun initializeKeys(view: View) {
        val whiteKeysPlayer1 = whiteKeyIds.map { keyId ->
            view.findViewById<Button>(keyId).apply {
                setOnTouchListener(createKeyTouchListener(keyId, pianoKeyPlayerHelper1))
            }
        }

        val blackKeysPlayer1 = blackKeyIds.map { keyId ->
            view.findViewById<Button>(keyId).apply {
                setOnTouchListener(createKeyTouchListener(keyId, pianoKeyPlayerHelper1))
            }
        }

        keysPlayer1 = (whiteKeysPlayer1 + blackKeysPlayer1).toTypedArray()

        val whiteKeysPlayer2 = whiteKeyIds2.map { keyId ->
            view.findViewById<Button>(keyId).apply {
                setOnTouchListener(createKeyTouchListener(keyId, pianoKeyPlayerHelper2))
            }
        }

        val blackKeysPlayer2 = blackKeyIds2.map { keyId ->
            view.findViewById<Button>(keyId).apply {
                setOnTouchListener(createKeyTouchListener(keyId, pianoKeyPlayerHelper2))
            }
        }

        keysPlayer2 = (whiteKeysPlayer2 + blackKeysPlayer2).toTypedArray()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createKeyTouchListener(keyId: Int, pianoKeyPlayerHelper: PianoKeyPlayerHelper) = View.OnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.isPressed = true
                pianoKeyPlayerHelper.playSound(keyId)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                view.isPressed = false
            }
        }
        true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pianoKeyPlayerHelper1.release()
        pianoKeyPlayerHelper2.release()
    }
}