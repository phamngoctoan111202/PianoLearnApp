package com.noatnoat.pianoapp.ui.callbacks

interface SongTutorialListener {
    fun onNoteChange(noteIds: List<Int>)
    fun onSongComplete()
}