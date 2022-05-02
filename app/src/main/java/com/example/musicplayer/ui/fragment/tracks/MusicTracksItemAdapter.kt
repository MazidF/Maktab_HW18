package com.example.musicplayer.ui.fragment.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemTracksBinding
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.setup

class MusicTracksItemAdapter(
    private val onClick: () -> Unit = {}
) : ListAdapter<Music, MusicTracksItemAdapter.MusicHolder>(
    DIFF_ITEM_CALLBACK
) {

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

    inner class MusicHolder(
        private val binding: MusicItemTracksBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                musicItemSelect.setup()
            }
        }

        fun bind(music: Music) = with(binding) {
            musicItemName.text = music.name
            Glide.with(root)
                .load(Constants.ALBUM_ART_PATH + "/" + music.albumId)
                .error(R.drawable.music_player_icon)
                .into(musicItemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val binding = MusicItemTracksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        holder.bind(getItem(position))
    }

}