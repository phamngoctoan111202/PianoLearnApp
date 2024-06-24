package com.noatnoat.pianoapp.helpers.piano

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayerHelper(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun playMusicFromPath(path: String) {
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(path))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        player.release()
    }
}