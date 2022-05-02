package com.example.musicplayer.utils.views.tab_layout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import com.example.musicplayer.R
import com.example.musicplayer.databinding.CustomTabLayoutBinding

class CustomTabLayout(
    context: Context,
    attributes: AttributeSet
) : LinearLayout(context, attributes) {
    private val items: Array<String>
    private lateinit var selected: View

    init {
        context.theme.obtainStyledAttributes(
            attributes,
            R.styleable.CustomTabLayout,
            0,
            0
        ).apply {
            val id = getResourceId(R.styleable.CustomTabLayout_items, 0)
            items = context.resources.getStringArray(id)
            recycle()
        }

        init(context)
    }

    private fun init(context: Context) {
        val tabFrameLayout = CustomTabLayoutBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        )

        val param = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            setMargins(5, 0, 5, 0)
        }

        with(tabFrameLayout) {
            var textView: TextView
            repeat(items.size) {
                textView = TextView(context).apply {
                    text = items[it]
                    layoutParams = param
                }
                customTableNames.addView(textView)
                customTableItems.addView(View(context).apply {
                    layoutParams = param
                })
            }
        }

        addView(tabFrameLayout.root.apply {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        })
    }

}