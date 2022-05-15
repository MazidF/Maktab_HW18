package com.example.musicplayer.ui.fragment.albums.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemAlbumsBinding
import com.example.musicplayer.databinding.MusicItemTracksBinding
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.ui.fragment.tracks.MusicTracksItemAdapter
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.secondToTimeFormatter
import com.example.musicplayer.utils.set
import com.example.musicplayer.utils.vibrate
import com.example.musicplayer.views.music_items.MusicItemAlbumsView
import kotlinx.coroutines.*

class MusicAlbumsItemAdapter(
    private val onItemClick: (Music, Int) -> Unit,
    private val onMoreClick: (Music) -> Unit,
    private val lifecycleOwner: LifecycleOwner
) : MusicItemAdapter<MusicAlbumsItemAdapter.MusicAlbumHolder>() {

    private val isSelected by lazy {
        MutableLiveData<Boolean>()
    }
    private var selections: HashSet<Long>? = null

    init {
        isSelected.observe(lifecycleOwner) {
            selections = null
        }
    }

    inner class MusicAlbumHolder(
        private val binding: MusicItemAlbumsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var music: Music? = null

        init {
            with(binding) {
                isSelected.observe(lifecycleOwner) {
                    val isActive = it == true
                    musicItemSelect.isVisible = isActive
                    musicItemMore.isVisible = isActive.not()
                    if (it == false) {
                        select(false)
                    }
                }
                root.setOnLongClickListener {
                    if (isSelected.value != true) {
                        selections = HashSet<Long>(20).apply {
                            music?.id?.let { id ->
                                add(id)
                            }
                        }
                        select(true)
                        root.context.vibrate()
                        isSelected.value = true
                    }
                    true
                }
                root.setOnClickListener {
                    selections?.let {
                        if (it.remove(music!!.id)) {
                            select(false)
                        } else {
                            select(it.add(music!!.id))
                        }
                        return@setOnClickListener
                    }
                    music?.run {
                        onItemClick(this, bindingAdapterPosition)
                    }
                }
                musicItemMore.setOnClickListener {

                }
            }
        }

        fun selectionBind(music: Music, isSelected: Boolean) {
            select(isSelected)
            bind(music)
        }

        fun bind(music: Music) {
            this.music = music
            setMusic(music)
        }

        private fun setMusic(music: Music) = with(binding) {
            musicItemName.text = music.name
            musicItemTime.text = music.time.secondToTimeFormatter()
        }

        fun select(isSelected: Boolean) = with(binding) {
            musicItemSelect.set(isSelected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAlbumHolder {
        val binding = MusicItemAlbumsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MusicAlbumHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicAlbumsItemAdapter.MusicAlbumHolder, position: Int) {
        getItem(position)?.let { music ->
            selections?.let { set ->
                holder.selectionBind(music, set.contains(music.id))
            } ?: run {
                holder.bind(music)
            }
        }
    }

    fun clearSelection(): Boolean {
        if (isSelected.value == true) {
            isSelected.value = false
            return true
        }
        return false
    }
}