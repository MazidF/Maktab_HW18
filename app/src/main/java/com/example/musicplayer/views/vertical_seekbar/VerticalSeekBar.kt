package com.example.musicplayer.views.vertical_seekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.example.musicplayer.R

/**
 * Vertical Progress Bar For Android Apps
 */
class VerticalSeekBar : AppCompatSeekBar {

    var visibleCallback: ((Boolean) -> Unit)? = null

    private val thumbDrawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.scrollbar_thumb)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }
    constructor(
        context: Context,
        attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        progressDrawable = null
        thumb = null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(c: Canvas) {
        c.rotate(-90f)
        c.translate(-height.toFloat(), 0f)
        super.onDraw(c)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                progress = max - (max * event.y / height).toInt()
                onSizeChanged(width, height, 0, 0)
            }
        }
        when(event.action) {
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                thumb = null
                visibleCallback?.invoke(false)
            }
            MotionEvent.ACTION_DOWN -> {
                thumb = thumbDrawable
                visibleCallback?.invoke(true)
            }
        }
        return true
    }
}
