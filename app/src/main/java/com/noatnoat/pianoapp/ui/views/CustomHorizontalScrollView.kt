package com.noatnoat.pianoapp.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class CustomHorizontalScrollView(context: Context, attrs: AttributeSet) : HorizontalScrollView(context, attrs) {
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}