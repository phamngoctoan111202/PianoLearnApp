package com.noatnoat.pianoapp.ui.extensions

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.noatnoat.pianoapp.R

class CustomSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    private val thumbWidth: Int = thumb.intrinsicWidth
    private val seekbarBackground: Drawable? = ContextCompat.getDrawable(context, R.drawable.seekbar_background)
    private val transparentBackground: Drawable? = ContextCompat.getDrawable(context, R.drawable.transparent_thumb)

    override fun onDraw(canvas: Canvas) {
        val thumbPos = thumb.bounds.left + thumbWidth / 2
        seekbarBackground?.setBounds(0, 0, thumbPos, height)
        transparentBackground?.setBounds(thumbPos, 0, width, height)

        seekbarBackground?.draw(canvas)
        transparentBackground?.draw(canvas)

        super.onDraw(canvas)
    }
}