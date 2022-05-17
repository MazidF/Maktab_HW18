package com.example.musicplayer.ui.fragment

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.model.Music

abstract class MusicItemAdapter<T : RecyclerView.ViewHolder> :
    PagedListAdapter<Music, T>(DIFF_ITEM_CALLBACK) {

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(
                oldItem: Music,
                newItem: Music
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Music,
                newItem: Music
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
