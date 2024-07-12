package com.noatnoat.pianoapp.helpers

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.content.pm.PackageManager
import androidx.appcompat.widget.SwitchCompat

class PermissionHelper(private val fragment: Fragment, private val context: Context) {

    fun requestPermission(requestCode: Int) {
        val permissionsToRequest = mutableListOf<String>()
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_MUSIC -> {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                        permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
            PERMISSION_REQUEST_RECORD_AUDIO -> {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && requestCode == PERMISSION_REQUEST_ACCESS_MUSIC) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            fragment.requestPermissions(permissionsToRequest.toTypedArray(), requestCode)
        }
    }

    fun handleRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray, switchAccessMusic: SwitchCompat, switchRecordAudio: SwitchCompat) {
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_MUSIC -> {
                switchAccessMusic.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            PERMISSION_REQUEST_RECORD_AUDIO -> {
                switchRecordAudio.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST_ACCESS_MUSIC = 101
        const val PERMISSION_REQUEST_RECORD_AUDIO = 102
    }
}