package com.example.musicplayer.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemTracksBinding

abstract class MusicItemAdapter : ListAdapter<Music, MusicItemAdapter.MusicHolder>(
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

    abstract inner class MusicHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(music: Music)
    }


    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        holder.bind(getItem(position))
    }

}