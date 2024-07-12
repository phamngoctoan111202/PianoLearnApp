package com.noatnoat.pianoapp.helpers.piano

import android.content.Context
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.util.SparseIntArray
import android.view.View
import android.widget.Button
import com.noatnoat.pianoapp.models.PianoKeys
import com.noatnoat.pianoapp.ui.callbacks.PianoScrollListener

class AutoPlayPianoHelper(
    private val context: Context,
    private val pianoKeys: PianoKeys,
    private val rootView: View,
    private val scrollListener: PianoScrollListener
) {
    private var soundPool: SoundPool = SoundPool.Builder().setMaxStreams(10).build()
    private var soundIds: SparseIntArray = SparseIntArray()
    private val handler = Handler(Looper.getMainLooper()) // Make handler a class-level property

    init {
        loadSounds()
    }

    private fun loadSounds() {
        pianoKeys.whiteKeys.forEach { key ->
            soundIds.put(key.keyId, soundPool.load(context, key.soundId, 1))
        }
        pianoKeys.blackKeys.forEach { key ->
            soundIds.put(key.keyId, soundPool.load(context, key.soundId, 1))
        }
    }

    private fun playKey(keyId: Int) {
        soundPool.play(soundIds[keyId], 1f, 1f, 0, 0, 1f)
    }

    fun autoPlay(sequence: List<Int>) {
        var delay = 0L
        val baseDelay = 1000L // Base delay
        val speed = PianoSettingHelper.getSpeed(context)
        val delayIncrement = baseDelay - (speed * 10) // Adjust delay based on speed

        sequence.forEachIndexed { _, keyId ->
            handler.postDelayed({
                scrollToKey(keyId)
                setKeyPressedState(keyId, true)
                playKey(keyId)
            }, delay)

            handler.postDelayed({
                setKeyPressedState(keyId, false)
            }, delay + delayIncrement - 100L) // Assuming 100L is the visibility delay

            delay += delayIncrement
        }
    }

    private fun setKeyPressedState(keyId: Int, pressed: Boolean) {
        val keyButton = rootView.findViewById<Button>(keyId)
        keyButton.isPressed = pressed
        keyButton.invalidate()
    }

    private fun scrollToKey(keyId: Int) {
        scrollListener.scrollToKey(keyId)
    }

    fun stopAutoPlay() {
        handler.removeCallbacksAndMessages(null)
    }

    fun release() {
        soundPool.release()
    }
}