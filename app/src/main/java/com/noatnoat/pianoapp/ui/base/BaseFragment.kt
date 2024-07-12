package com.noatnoat.pianoapp.ui.base

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.noatnoat.pianoapp.ui.activities.MainActivity

open class BaseFragment : Fragment() {

    fun setStatusBarColor(@ColorInt color: Int) {
        val window = activity?.window
        window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                it.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            it.statusBarColor = color
        }
    }

    fun makeStatusBarTransparent() {
        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }




    fun setFragmentLandscapeOrientation() {
        (activity as? BaseActivity)?.setLandscapeOrientation()
    }

    fun setFragmentPortraitOrientation() {
        (activity as? BaseActivity)?.setPortraitOrientation()
    }
}