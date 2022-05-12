package com.example.musicplayer.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedListAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.views.music_items.MusicItemView

abstract class MusicItemAdapter(
    private val onItemClick: (Music) -> Unit
) : PagedListAdapter<Music, MusicItemAdapter.MusicHolder>(DIFF_ITEM_CALLBACK) {
    private var tracker: SelectionTracker<Long>? = null

    fun setTracker(tracker: SelectionTracker<Long>) {
        this.tracker = tracker
    }

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

    init {
        setHasStableIds(true)
        tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
            }
        })
    }

    final override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int) = position.toLong()

    private val isSelecting by lazy {
        MutableLiveData(false)
    }

    abstract inner class MusicHolder(
        private val view: MusicItemView
    ) : RecyclerView.ViewHolder(view) {
        private var music: Music? = null
        init {
            with(view) {
                setOnClickListener {
                    music?.let(onItemClick)
                }
            }
        }

        fun innerBind(music: Music, isSelected: Boolean) {
            this.music = music
            view.isActivated = isSelected
            bind(music)
        }

        abstract fun bind(music: Music)

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long = itemId
            }
        }
    }

    fun selectAll(selected: Boolean) {
        tracker?.setItemsSelected(0L..(currentList?.size ?: 0), selected)
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        getItem(position)?.let { music ->
            tracker?.let { selection ->
                return holder.innerBind(music, selection.isSelected(position.toLong()))
            }
            holder.bind(music)
        }
    }
}
