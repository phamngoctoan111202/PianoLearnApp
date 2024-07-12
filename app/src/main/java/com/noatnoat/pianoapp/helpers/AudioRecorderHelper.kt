package com.noatnoat.pianoapp.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.noatnoat.pianoapp.enums.MusicalInstrument
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import com.noatnoat.pianoapp.models.Record

class AudioRecorderHelper(private val activity: Context, private val recordTimeTextView: TextView) {

    private var mediaRecorder: MediaRecorder? = null
    private var output: String? = null
    private var timer: CountDownTimer? = null
    private var isRecording = false


    fun startRecording() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.d("AudioRecorderHelper", "Requesting RECORD_AUDIO permission")
            ActivityCompat.requestPermissions(activity as Activity, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            try {
                Log.d("AudioRecorderHelper", "Setting up MediaRecorder")
                setupMediaRecorder()
                Log.d("AudioRecorderHelper", "Starting timer")
                startTimer()
                isRecording = true
                Log.d("AudioRecorderHelper", "Recording started successfully")
            } catch (e: Exception) {
                Log.e("AudioRecorderHelper", "Error starting recording", e)
            }
        }
    }

    fun getRecordList(): List<Record> {
        val records = mutableListOf<Record>()
        val musicDir = Environment.getExternalStorageDirectory().absolutePath + "/Musics"
        val musicFolder = File(musicDir)

        if (musicFolder.exists()) {
            val files = musicFolder.listFiles()
            files?.forEach { file ->
                if (file.isFile && file.name.endsWith(".mp3")) {
                    val metadataRetriever = MediaMetadataRetriever()
                    metadataRetriever.setDataSource(file.absolutePath)
                    val name = file.nameWithoutExtension
                    val duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let { duration ->
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration.toLong())
                    } ?: "Unknown"
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(file.lastModified())
                    val path = file.absolutePath
                    records.add(Record(name, duration, date, path, MusicalInstrument.PIANO))
                }
            }
        }
        return records
    }

    private fun setupMediaRecorder() {
        val resolver = activity.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Piano_${System.currentTimeMillis()}.wav")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
        }

        val audioUri: Uri? = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            audioUri?.let {
                val parcelFileDescriptor = resolver.openFileDescriptor(it, "w", null)
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(parcelFileDescriptor?.fileDescriptor)
                    prepare()
                    start()
                    Log.d("AudioRecorderHelper", "MediaRecorder started")
                }
            } ?: run {
                Log.e("AudioRecorderHelper", "Failed to create new MediaStore record")
            }
        } catch (e: IOException) {
            Log.e("AudioRecorderHelper", "MediaRecorder configuration failed", e)
        }
    }

    fun stopRecording() {
        if (isRecording) {
            try {
                mediaRecorder?.stop()
                isRecording = false
            } catch (e: IllegalStateException) {
                Log.e("AudioRecorderHelper", "Attempted to stop recording when it was not in a valid state", e)
            } catch (e: RuntimeException) {
                Log.e("AudioRecorderHelper", "No valid audio/video data has been received", e)
                File(output ?: "").delete() // Delete the output file if it's invalid
            } finally {
                timer?.cancel()
            }
        }
    }

    fun saveRecording() {
        if (output != null) {
            val file = File(output!!)
            if (file.exists()) {
                Log.d("AudioRecorderHelper", "File was saved successfully at $output")
            } else {
                Log.d("AudioRecorderHelper", "Failed to save file at $output")
            }
        }
    }

    private fun startTimer() {
        var timeInSeconds = 0
        timer = object: CountDownTimer(Long.MAX_VALUE, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                timeInSeconds++
                val seconds = timeInSeconds % 60
                val minutes = timeInSeconds / 60
                recordTimeTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
            }
        }
        (timer as CountDownTimer).start()
    }


    companion object {
        const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}