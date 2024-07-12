package com.noatnoat.pianoapp.ui.base

import androidx.appcompat.app.AppCompatActivity
import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.noatnoat.pianoapp.R

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStatusBarGradiant(this)
    }

//    fun setStatusBarGradiant(activity: Activity) {
//        val window: Window = activity.window
//        val background = ContextCompat.getDrawable(activity, R.drawable.gradient_theme)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(activity, android.R.color.transparent)
//        window.navigationBarColor = ContextCompat.getColor(activity, android.R.color.transparent)
//        window.setBackgroundDrawable(background)
//    }

    @TargetApi(Build.VERSION_CODES.R)
    fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }
    }

// Trong BaseActivity.kt

    fun setLandscapeOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun setPortraitOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}