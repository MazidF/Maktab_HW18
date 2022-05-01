package com.example.musicplayer.ui.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemTracksBinding

class MusicTracksItemAdapter : ListAdapter<Music, MusicTracksItemAdapter.MusicHolder>(
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
        fun bind(music: Music) {
            binding.itemText.text = music.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val binding = MusicItemTracksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun addAndSubmitList(list: List<Music>) {
        submitList(currentList + list)

    }

}