package com.example.musicplayer.views.music_items

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

abstract class MusicItemView : LinearLayout {

    @JvmOverloads constructor(context: Context, resource: Int) : super(context) {
        val view = inflate(context, resource, this)
        onViewCreated(view)
    }

    @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet,
        resource: Int
    ) : super(context, attr) {
        val view = inflate(context, resource, this)
        onViewCreated(view)
    }

    abstract fun onViewCreated(view: View)

    abstract fun changeSelectionState(isActive: Boolean)

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        changeSelectionState(activated)
    }

    abstract fun selected(isSelected: Boolean)
}