package com.example.musicplayer.utils.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.musicplayer.R

class CustomTabLayout(
    context: Context,
    attributes: AttributeSet
) : LinearLayout(context, attributes) {
    private val itemCount: Int

    init {
        children
        context.theme.obtainStyledAttributes(
            attributes,
            R.styleable.CustomTabLayout,
            0,
            0
        ).apply {
            itemCount = getInteger(R.styleable.CustomTabLayout_itemCount, 3)
        }
    }

}