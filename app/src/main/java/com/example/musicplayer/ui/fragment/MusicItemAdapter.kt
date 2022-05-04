package com.example.musicplayer.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.utils.logger
import java.util.*

abstract class MusicItemAdapter: ListAdapter<Music, MusicItemAdapter.MusicHolder>(
    DIFF_ITEM_CALLBACK
) {

    final override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    protected var isSelecting = MutableLiveData<Boolean>()
    fun isSelecting() = isSelecting.value == true

    private val selectedSet by lazy {
        TreeSet<Int>()
    }

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem == newItem
            }
        }
    }

    abstract inner class MusicHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            isSelecting.observeForever {
                logger("$absoluteAdapterPosition , $adapterPosition, $bindingAdapterPosition")
                onSelectingChange(it == true)
            }
            binding.root.setOnLongClickListener {
                if (!isSelecting()) {
                    isSelecting.value = true
                }
                false
            }
        }

        fun select() {
            selectedSet.add(adapterPosition)
        }

        fun unselect() {
            selectedSet.remove(adapterPosition)
        }

        abstract fun onSelectingChange(isSelecting: Boolean)

        abstract fun bind(music: Music, isSelected: Boolean)
    }

    fun selectAll(selected: Boolean) {
        if (selected) {
            selectedSet.addAll(List(itemCount) {it})
        } else {
            selectedSet.clear()
        }
        isSelecting.value = selected
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        holder.bind(getItem(position), selectedSet.contains(position))
    }
}