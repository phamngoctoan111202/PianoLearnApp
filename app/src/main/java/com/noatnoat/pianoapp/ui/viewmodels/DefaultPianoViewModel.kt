package com.noatnoat.pianoapp.ui.viewmodels

import android.app.Application
import android.util.SparseIntArray
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noatnoat.pianoapp.models.PianoKeyMapping
import com.noatnoat.pianoapp.models.PianoKeys
import com.noatnoat.pianoapp.models.Song
import com.noatnoat.pianoapp.models.SongRepository
import java.util.LinkedList
import java.util.Queue

class DefaultPianoViewModel(application: Application) : AndroidViewModel(application) {
    private val pianoKeys = PianoKeys.getInstance()
    val whiteKeyIds = pianoKeys.whiteKeys.map { it.keyId }.toTypedArray()
    val blackKeyIds = pianoKeys.blackKeys.map { it.keyId }.toTypedArray()
    val whiteKeySounds = pianoKeys.whiteKeys.map { it.soundId }.toTypedArray()
    val blackKeySounds = pianoKeys.blackKeys.map { it.soundId }.toTypedArray()

    val soundIds = SparseIntArray()
    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> = _isRecording

    val queue: Queue<Int> = LinkedList()
    private val _isInLearnMode = MutableLiveData<Boolean>()
    val isInLearnMode: LiveData<Boolean> = _isInLearnMode

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    val songList = MutableLiveData<List<Song>>()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        val songRepository = SongRepository()
        songList.value = songRepository.songs
    }

    fun toggleRecording() {
        _isRecording.value = !(_isRecording.value ?: false)
    }

    fun toggleLearnMode() {
        _isInLearnMode.value = !(_isInLearnMode.value ?: false)
    }

    fun togglePlaying() {
        _isPlaying.value = !(_isPlaying.value ?: false)
    }

    fun getKeyIdsForSong(songName: String): Queue<Int> {
        val song = songList.value?.find { it.name == songName }
        val keyIdsQueue = LinkedList<Int>()

        song?.let {
            for (note in it.notes) {
                PianoKeyMapping.noteToKeyIdMap[note]?.let { keyId ->
                    keyIdsQueue.offer(keyId)
                }
            }
        }

        return keyIdsQueue
    }

    // Additional ViewModel methods for other logic as needed
}
