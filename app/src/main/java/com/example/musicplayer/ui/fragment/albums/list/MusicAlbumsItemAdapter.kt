package com.example.musicplayer.ui.fragment.albums.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemAlbumsBinding
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.utils.*

class MusicAlbumsItemAdapter(
    private val onClick: (Unit) -> Unit = {}
) : MusicItemAdapter() {

    private inner class MusicAlbumHolder(
        private val binding: MusicItemAlbumsBinding
    ) : MusicHolder(binding) {
        init {
            with(binding) {
                musicItemSelect.setup {
                    if (isSelected) {
                        select()
                    } else {
                        unselect()
                    }
                }
            }
        }

        override fun onSelectingChange(isSelecting: Boolean) {
            with(binding) {
                if (isSelecting) {
                    musicItemMore.gone()
                    musicItemSelect.visible()
                } else {
                    musicItemSelect.gone()
                    musicItemMore.visible()
                }
            }
        }

        override fun bind(music: Music, isSelected: Boolean) {
            with(binding) {
                musicItemName.text = music.name
                musicItemTime.text = music.time.secondToTimeFormatter()
                musicItemSelect.set(isSelected)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val binding =
            MusicItemAlbumsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicAlbumHolder(binding)
    }
}