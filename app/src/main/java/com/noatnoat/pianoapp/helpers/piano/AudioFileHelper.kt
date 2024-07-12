package com.noatnoat.pianoapp.helpers.piano

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

class AudioFileHelper(private val context: Context) {

    fun getAllMusic(): ArrayList<String> {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.AudioColumns.DATA)
        val selection = MediaStore.Audio.AudioColumns.DATA + " LIKE ?"
        val selectionArgs = arrayOf("%.mp3")
        val songsPath = ArrayList<String>()
        val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)

        cursor?.let {
            while (it.moveToNext()) {
                val path = it.getString(0)
                songsPath.add(path)
            }
            it.close()
        }
        return songsPath
    }
}