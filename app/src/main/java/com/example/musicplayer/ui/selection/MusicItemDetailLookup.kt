package com.example.musicplayer.ui.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.ui.fragment.MusicItemAdapter

class MusicItemDetailLookup(
    private val recyclerView: RecyclerView
) : ItemDetailsLookup<Long>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y) ?: return null
        val holder = recyclerView.getChildViewHolder(view)
        return (holder as MusicItemAdapter.MusicHolder).getItemDetails()
    }
}