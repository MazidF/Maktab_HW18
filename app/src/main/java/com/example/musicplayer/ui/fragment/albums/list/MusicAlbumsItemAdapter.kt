package com.example.musicplayer.ui.fragment.albums.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemAlbumsBinding
import com.example.musicplayer.databinding.MusicItemTracksBinding
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.secondToTimeFormatter
import com.example.musicplayer.utils.timeFormatter

class MusicAlbumsItemAdapter(
    private val onClick: () -> Unit = {}
) : MusicItemAdapter() {

    private inner class MusicAlbumHolder(
        private val binding: MusicItemAlbumsBinding
    ): MusicHolder(binding) {
        override fun bind(music: Music): Unit = with(binding) {
            musicItemName.text = music.name
            musicItemTime.text = music.time.secondToTimeFormatter()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val binding = MusicItemAlbumsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicAlbumHolder(binding)
    }

}