package com.example.musicplayer.ui.fragment.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemTracksBinding
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.ui.fragment.paging_adapter.MusicPagingAdapter
import com.example.musicplayer.ui.model.SelectableMusic
import com.example.musicplayer.utils.*

class MusicTracksItemAdapter(
    private val onClick: () -> Unit = {}
) : MusicPagingAdapter() {

    private inner class MusicTracksHolder(
        private val binding: MusicItemTracksBinding
    ) : MusicPagingHolder(binding) {
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
            Glide.with(root)
                .applyDefaultRequestOptions(Constants.glideDiskCacheStrategy)
                .load(Constants.ALBUM_ART_PATH + "/" + music.albumId)
                .error(R.drawable.music_player_icon)
                .into(musicItemImage)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicPagingHolder {
        val binding =
            MusicItemTracksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicTracksHolder(binding)
    }

    fun scrollToFirst(filter: (Music) -> Boolean, block: (Int) -> Unit) {
        val index = currentList?.indexOfFirst {
            filter(it.music)
        } ?: return
        block(index)
    }

    override fun onClickItem() {
        onClick()
    }
}