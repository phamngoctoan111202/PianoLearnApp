package com.noatnoat.pianoapp.helpers.piano

import android.annotation.SuppressLint
import android.content.Context
import android.media.SoundPool
import android.util.SparseIntArray


class SoundPoolHelper private constructor(private val context: Context) {
    private lateinit var soundPool: SoundPool
    private lateinit var soundIds: SparseIntArray
    private var isLoaded = false
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: SoundPoolHelper? = null

        fun getInstance(context: Context): SoundPoolHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundPoolHelper(context).also { INSTANCE = it }
            }
        }
    }

    fun loadSoundsIfNecessary(keySoundPairs: Array<Pair<Int, Int>>) {
        if (!isLoaded) {
            soundPool = SoundPool.Builder().setMaxStreams(10).build()
            soundIds = SparseIntArray()

            keySoundPairs.forEach { pair ->
                soundIds.put(pair.first, soundPool.load(context, pair.second, 1))
            }
            isLoaded = true
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