package com.noatnoat.pianoapp.helpers.piano

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import android.util.Log

class MusicPlayerHelper(private val context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun playMusicFromPath(path: String) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            playMusicUsingMediaStore(path)
        } else {
            startPlayback(path)
        }
    }

    private fun playMusicUsingMediaStore(path: String) {
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = MediaStore.Audio.Media.DATA + "=?"
        val selectionArgs = arrayOf(path)
        val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val cursor = context.contentResolver.query(queryUri, projection, selection, selectionArgs, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val contentUri: Uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getString(idColumn))
                startPlayback(contentUri.toString())
            } else {
                Log.e("MusicPlayerHelper", "File not found in MediaStore: $path")
            }
        }
    }

    private fun startPlayback(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            val mediaItem: MediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e("MusicPlayerHelper", "Error playing music: $uriString", e)
        }
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        player.release()
    }
}