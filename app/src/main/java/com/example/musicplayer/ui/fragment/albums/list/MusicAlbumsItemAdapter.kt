package com.example.musicplayer.ui.fragment.albums.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemAlbumsBinding
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.ui.model.SelectableMusic
import com.example.musicplayer.utils.*

class MusicAlbumsItemAdapter(
    private val onClick: () -> Unit = {}
) : MusicItemAdapter() {

    private inner class MusicAlbumHolder(
        private val binding: MusicItemAlbumsBinding
    ) : MusicHolder(binding) {
        private var wasSelecting = false

        init {
            with(binding) {
                musicItemSelect.setup {
                    selectOrUnselect()
                }
            }
        }

        override fun manualSelect() {
            binding.musicItemSelect.performClick()
        }

        private fun baseBind(music: Music) = with(binding) {
            musicItemName.text = music.name
            musicItemTime.text = music.time.secondToTimeFormatter()
        }

        override fun bind(music: Music) {
            with(binding) {
                baseBind(music)
                if (wasSelecting) {
                    musicItemSelect.gone()
                    musicItemMore.visible()
                    wasSelecting = false
                }
            }
        }

        override fun bind(selectableMusic: SelectableMusic) {
            val isSelected = selectableMusic.isSelected
            with(binding) {
                if (isSelected == null || !wasSelecting) {
                    musicItemMore.gone()
                    musicItemSelect.visible()
                    wasSelecting = true
                    if (isSelected == null) {
                        selectableMusic.isSelected = false
                    }
                }
                val music = selectableMusic.music
                baseBind(music)
                musicItemSelect.set(selectableMusic.isSelected!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val binding =
            MusicItemAlbumsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicAlbumHolder(binding)
    }

    override fun onClickItem() {
        onClick()
    }
}