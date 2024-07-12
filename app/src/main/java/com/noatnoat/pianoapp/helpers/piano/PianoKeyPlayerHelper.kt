package com.noatnoat.pianoapp.helpers.piano

import android.content.Context
import android.media.SoundPool
import android.util.SparseIntArray

class PianoKeyPlayerHelper(private val context: Context) {
    private lateinit var soundPool: SoundPool
    private lateinit var soundIds: SparseIntArray

    fun loadSounds(whiteKeySounds: Array<Int>, blackKeySounds: Array<Int>, whiteKeyIds: Array<Int>, blackKeyIds: Array<Int>) {
        soundPool = SoundPool.Builder().setMaxStreams(10).build()
        soundIds = SparseIntArray()

        whiteKeySounds.forEachIndexed { index, soundRes ->
            soundIds.put(whiteKeyIds[index], soundPool.load(context, soundRes, 1))
        }

        blackKeySounds.forEachIndexed { index, soundRes ->
            soundIds.put(blackKeyIds[index], soundPool.load(context, soundRes, 1))
        }
    }

    fun playSound(keyId: Int) {
        val soundId = soundIds.get(keyId)
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}