package com.noatnoat.pianoapp.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noatnoat.pianoapp.R
import android.widget.Button
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.widget.SeekBar
import android.graphics.Color
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.util.SparseIntArray
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import com.noatnoat.pianoapp.helpers.piano.PianoKeyPlayer
import com.noatnoat.pianoapp.ui.activities.MainActivity


class PianoFragment : Fragment() {
    private lateinit var soundPool: SoundPool
    private lateinit var soundIds: SparseIntArray
    private lateinit var pianoKeyPlayer: PianoKeyPlayer

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

    private val whiteKeySounds = arrayOf(
        R.raw.a1, R.raw.b1, R.raw.c1, R.raw.d1, R.raw.e1, R.raw.f1, R.raw.g1,
        R.raw.a2, R.raw.b2, R.raw.c2, R.raw.d2, R.raw.e2, R.raw.f2, R.raw.g2,
        R.raw.a3, R.raw.b3, R.raw.c3, R.raw.d3, R.raw.e3, R.raw.f3, R.raw.g3,
        R.raw.a4, R.raw.b4, R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4,
        R.raw.a5, R.raw.b5, R.raw.c5, R.raw.d5, R.raw.e5, R.raw.f5, R.raw.g5,
        R.raw.a6, R.raw.b6, R.raw.c6, R.raw.d6, R.raw.e6, R.raw.f6, R.raw.g6,
        R.raw.a7, R.raw.b7, R.raw.c7, R.raw.d7, R.raw.e7, R.raw.f7, R.raw.g7
    )

    private val blackKeySounds = arrayOf(
        R.raw.a11, R.raw.c11, R.raw.d11, R.raw.f11, R.raw.g11,
        R.raw.a22, R.raw.c22, R.raw.d22, R.raw.f22, R.raw.g22,
        R.raw.a33, R.raw.c33, R.raw.d33, R.raw.f33, R.raw.g33,
        R.raw.a44, R.raw.c44, R.raw.d44, R.raw.f44, R.raw.g44,
        R.raw.a55, R.raw.c55, R.raw.d55, R.raw.f55, R.raw.g55,
        R.raw.a66, R.raw.c66, R.raw.d66, R.raw.f66, R.raw.g66,
        R.raw.a77, R.raw.c77, R.raw.d77, R.raw.f77, R.raw.g77
    )

    private lateinit var keys: Array<Button>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).setLandscapeOrientation()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        soundPool = SoundPool.Builder().setMaxStreams(10).build()
        soundIds = SparseIntArray()
        val view = inflater.inflate(R.layout.fragment_piano, container, false)
        pianoKeyPlayer = PianoKeyPlayer(requireContext())
        pianoKeyPlayer.loadSounds(whiteKeySounds, blackKeySounds, whiteKeyIds, blackKeyIds)
        whiteKeySounds.forEachIndexed { index, soundRes ->
            soundIds.put(whiteKeyIds[index], soundPool.load(context, soundRes, 1))
        }

        blackKeySounds.forEachIndexed { index, soundRes ->
            soundIds.put(blackKeyIds[index], soundPool.load(context, soundRes, 1))
        }


        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        val scrollView = view.findViewById<HorizontalScrollView>(R.id.scrollView)
        scrollView.post { scrollView.scrollTo((scrollView.getChildAt(0).width * 0.55).toInt(), 0) }

        scrollView.setOnTouchListener { _, _ -> true }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val scrollX = (scrollView.getChildAt(0).width - scrollView.width) * progress / 100
                scrollView.scrollTo(scrollX, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed for this example
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed for this example
            }
        })

        val whiteKeys = whiteKeyIds.mapIndexed { index, keyId ->
            view.findViewById<Button>(keyId).apply {
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            setBackgroundColor(Color.parseColor("#ffde00"))
                            pianoKeyPlayer.playSound(keyId)
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            setBackgroundColor(Color.WHITE)
                        }
                    }
                    true
                }
            }
        }

        val blackKeys = blackKeyIds.mapIndexed { index, keyId ->
            view.findViewById<Button>(keyId).apply {
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            setBackgroundColor(Color.parseColor("#ffde00"))
                            pianoKeyPlayer.playSound(keyId)
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            setBackgroundColor(Color.BLACK)
                        }
                    }
                    true
                }
            }
        }

        keys = whiteKeys.toTypedArray() + blackKeys.toTypedArray()


        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pianoKeyPlayer.release()
    }

}
