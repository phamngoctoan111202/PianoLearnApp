package com.noatnoat.pianoapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.util.Date
import java.util.Locale
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.annotation.IdRes
import android.os.Bundle

fun NavController.navigateSafe(@IdRes resId: Int, args: Bundle? = null) {
    val destinationId = currentDestination?.getAction(resId)?.destinationId.orEmpty()
    currentDestination?.let { node ->
        val currentNode = when (node) {
            is NavGraph -> node
            else -> node.parent
        }
        if (destinationId != 0) {
            currentNode?.findNode(destinationId)?.let { navigate(resId, args) }
        }
    }
}

fun Int?.orEmpty(default: Int = 0): Int {
    return this ?: default
}


fun getStatusBarHeight(view: View): Int = ViewCompat.getRootWindowInsets(view)?.getInsets(
    WindowInsetsCompat.Type.systemBars()
)?.top ?: 0

fun getNavigationBarHeight(view: View): Int = ViewCompat.getRootWindowInsets(view)?.getInsets(
    WindowInsetsCompat.Type.systemBars()
)?.bottom ?: 0


fun Context.dpToPx(dp: Int): Int {
    val density = resources.displayMetrics.density
    return (dp * density).toInt()
}

fun getScreenWidth(context: Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets =
            windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val dm = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(dm)
        dm.widthPixels
    }
    return metrics
}

fun formatTimeAgo(timeInMillis: Long): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(timeInMillis, now, DateUtils.MINUTE_IN_MILLIS)
        .toString()
}

fun getPressureDiagnose(systolic: Int, diastolic: Int): Int {
    return when {
//        systolic < 90 || diastolic < 60 -> 0
//        systolic in 90..119 && diastolic in 60..79 -> 1
//        systolic in 120..139 && diastolic in 80..89 -> 2
//        systolic in 140..159 && diastolic in 90..99 -> 3
//        systolic in 160..179 && diastolic in 100..109 -> 4
//        systolic >= 180 || diastolic >= 110 -> 5
//        else -> 0
        systolic < 90 || diastolic < 60 -> 0
        systolic in 90..119 && diastolic in 60..79 -> 1
        systolic in 120..129 && diastolic < 80 -> 1
        (systolic in 130..139 || diastolic in 80..89) -> 2
        (systolic in 140..180 || diastolic in 90..120) -> 3
        systolic >= 181 || diastolic >= 121 -> 4
        else -> 1
    }
}

fun getBloodSugarDiagnostic(bloodSugar: Int): Int {
    return when {
        bloodSugar < 70 -> 0
        bloodSugar in 70..99 -> 1
        bloodSugar in 100..125 -> 2
        bloodSugar >= 126 -> 3
        else -> 0
    }
}


fun formatTime(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timeInMillis))
}

fun formatDate(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(timeInMillis))
}

fun formatDateWithoutYear(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    return formatter.format(Date(timeInMillis))
}

fun formatDateTime(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(timeInMillis))
}

fun currentDay(): Long {
    val now = Calendar.getInstance()
    now.set(Calendar.HOUR_OF_DAY, 23)
    now.set(Calendar.MINUTE, 59)
    now.set(Calendar.SECOND, 59)
    now.set(Calendar.MILLISECOND, 999)
    return now.timeInMillis
}

fun Number.roundTo() = "%.2f".format(this, Locale.getDefault())


class SharedPreferencesManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "com.noatnoat.pianoapp"
        private var instance: SharedPreferencesManager? = null

        fun getInstance(context: Context): SharedPreferencesManager {
            if (instance == null) {
                instance = SharedPreferencesManager(context)
            }
            return instance as SharedPreferencesManager
        }
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Add methods for other data types as needed

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}


class CustomBarChartRender(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) :
    BarChartRenderer(chart, animator, viewPortHandler) {
    private val mBarShadowRectBuffer = RectF()
    private var mRadius = 0
    fun setRadius(mRadius: Int) {
        this.mRadius = mRadius
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans: Transformer = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        mShadowPaint.color = dataSet.barShadowColor
        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY
        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor
            val barData = mChart.barData
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float
            var i = 0
            val count = Math.min(
                Math.ceil(
                    (dataSet.entryCount.toFloat() * phaseX).toDouble()
                        .toInt().toDouble()
                ), dataSet.entryCount.toDouble()
            )
            while (i < count) {
                val e = dataSet.getEntryForIndex(i)
                x = e.x
                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf
                trans.rectValueToPixel(mBarShadowRectBuffer)
                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break
                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()
                c.drawRoundRect(mBarRect, mRadius.toFloat(), mRadius.toFloat(), mShadowPaint)
                i++
            }
        }

        // initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)
        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }
        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.color = dataSet.getColor(j / 4)
            }
            val path2: Path = roundRect(
                RectF(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3]
                ), mRadius.toFloat(), mRadius.toFloat(), true, true, false, false
            )
            c.drawPath(path2, mRenderPaint)
            if (drawBorder) {
                val path: Path = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), mRadius.toFloat(), mRadius.toFloat(), true, true, false, false
                )
                c.drawPath(path, mBarBorderPaint)
            }
            j += 4
        }
    }

    private fun roundRect(
        rect: RectF,
        rx: Float,
        ry: Float,
        tl: Boolean,
        tr: Boolean,
        br: Boolean,
        bl: Boolean
    ): Path {
        var rx = rx
        var ry = ry
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom
        val path = Path()
        if (rx < 0) rx = 0f
        if (ry < 0) ry = 0f
        val width = right - left
        val height = bottom - top
        if (rx > width / 2) rx = width / 2
        if (ry > height / 2) ry = height / 2
        val widthMinusCorners = (width - (2 * rx))
        val heightMinusCorners = (height - (2 * ry))
        path.moveTo(right, top + ry)
        if (tr) path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
        else {
            path.rLineTo(0f, -ry)
            path.rLineTo(-rx, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (tl) path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
        else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, ry)
        }
        path.rLineTo(0f, heightMinusCorners)
        if (bl) path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
        else {
            path.rLineTo(0f, ry)
            path.rLineTo(rx, 0f)
        }
        path.rLineTo(widthMinusCorners, 0f)
        if (br) path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
        else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }
        path.rLineTo(0f, -heightMinusCorners)
        path.close() //Given close, last lineto can be removed.
        return path
    }
}

fun checkPermission(context: Context): Boolean {
    return false
}
