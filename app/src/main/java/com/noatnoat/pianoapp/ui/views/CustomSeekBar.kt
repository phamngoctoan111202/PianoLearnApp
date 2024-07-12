package com.noatnoat.pianoapp.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.noatnoat.pianoapp.R

class CustomSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    private val seekbarBackground: Drawable? = ContextCompat.getDrawable(context, R.drawable.seekbar_background)

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.transparent)
    }

    override fun onDraw(canvas: Canvas) {
        val seekBarWidth = width

        seekbarBackground?.setBounds(0, 0, seekBarWidth, height)
        seekbarBackground?.draw(canvas)

        val thumbRight = thumb.bounds.right.toFloat()
        val thumbLeft = thumb.bounds.left.toFloat()

        val backgroundRight = seekBarWidth.toFloat()

        canvas.drawRect(thumbRight, 0f, backgroundRight, height.toFloat(), paint)
        canvas.drawRect(0f, 0f, thumbLeft, height.toFloat(), paint)

        super.onDraw(canvas)
    }
}
