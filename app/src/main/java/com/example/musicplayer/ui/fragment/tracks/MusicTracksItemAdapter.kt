package com.example.musicplayer.ui.fragment.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemTracksBinding
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.setup

class MusicTracksItemAdapter(
    private val onClick: () -> Unit = {}
) : MusicItemAdapter() {

    private inner class MusicTracksHolder(
        private val binding: MusicItemTracksBinding
    ) : MusicHolder(binding) {
        init {
            with(binding) {
                musicItemSelect.setup()
            }
        }

        override fun bind(music: Music): Unit = with(binding) {
            musicItemName.text = music.name
            Glide.with(root)
                .load(Constants.ALBUM_ART_PATH + "/" + music.albumId)
                .error(R.drawable.music_player_icon)
                .into(musicItemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val binding = MusicItemTracksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicTracksHolder(binding)
    }

    fun scrollToFirst(filter: (Music) -> Boolean, block: (Int) -> Unit) {
        val index = currentList.indexOfFirst(filter)
        block(index)
    }
}