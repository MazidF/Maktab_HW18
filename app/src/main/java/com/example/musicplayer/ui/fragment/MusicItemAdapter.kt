package com.example.musicplayer.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.ui.model.SelectableMusic
import com.example.musicplayer.utils.vibrate

abstract class MusicItemAdapter :
    ListAdapter<SelectableMusic, MusicItemAdapter.MusicHolder>(DIFF_ITEM_CALLBACK) {

    private val _selectedCount by lazy {
        MutableLiveData<Int>()
    }

    protected var isSelecting = MutableLiveData<Boolean>()
    fun isSelecting() = isSelecting.value == true
    private var selectionStarter = -1

    init {
        isSelecting.observeForever {
            SelectableMusic.selectionStateChenged = true
            if (it == true) {
                selectionMode()
            } else {
                restoreMode()
            }
        }
    }

    private val submitCallback: () -> Unit = {
        SelectableMusic.selectionStateChenged = false
    }

    private fun restoreMode() {
        val list = currentList.map {
            SelectableMusic(it.music, false)
        }
        submitList(list, submitCallback)
    }

    fun removeSelection() {
        isSelecting.value = false
    }

    private fun selectionMode(default: Boolean? = null) {
        val list = currentList.map {
            SelectableMusic(it.music, default)
        }.apply {
            this[selectionStarter].isSelected = true
        }
        submitList(list, submitCallback)
    }

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<SelectableMusic>() {
            override fun areItemsTheSame(
                oldItem: SelectableMusic,
                newItem: SelectableMusic
            ): Boolean {
                return oldItem.music.id == newItem.music.id
            }

            override fun areContentsTheSame(
                oldItem: SelectableMusic,
                newItem: SelectableMusic
            ): Boolean {
                return SelectableMusic.selectionStateChenged.not() && oldItem == newItem
            }
        }
    }

    abstract inner class MusicHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.apply {
                setOnLongClickListener {
                    if (!isSelecting()) {
                        selectionStarter = bindingAdapterPosition
                        isSelecting.value = true
                        context.vibrate(100)
                    }
                    false
                }
                setOnClickListener {
                    if (isSelecting()) {
                        manualSelect()
                    } else {
                        onClickItem()
                    }
                }
            }
        }

        fun selectOrUnselect() {
            currentList[bindingAdapterPosition].selectAndUnSelect()
        }

        abstract fun manualSelect()

        abstract fun bind(music: Music)

        abstract fun bind(selectableMusic: SelectableMusic)
    }

    fun selectAll(selected: Boolean) {
        selectionMode(selected)
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        if (isSelecting()) {
            return holder.bind(getItem(position))
        }
        holder.bind(getItem(position).music)
    }

    abstract fun onClickItem()
}
