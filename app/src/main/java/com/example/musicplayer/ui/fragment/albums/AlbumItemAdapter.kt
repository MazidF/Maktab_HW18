package com.example.musicplayer.ui.fragment.albums

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.databinding.AlbumItemBinding
import com.example.musicplayer.data.model.AlbumInfo
import com.example.musicplayer.utils.Constants

class AlbumItemAdapter(
    private val onClick: (AlbumInfo) -> Unit = {}
) : ListAdapter<AlbumInfo, AlbumItemAdapter.AlbumHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<AlbumInfo>() {
            override fun areItemsTheSame(oldItem: AlbumInfo, newItem: AlbumInfo): Boolean {
                return newItem.album.id == oldItem.album.id
            }

            override fun areContentsTheSame(oldItem: AlbumInfo, newItem: AlbumInfo): Boolean {
                return newItem == oldItem
            }
        }
    }

    inner class AlbumHolder(
        private val binding: AlbumItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                root.setOnClickListener {
                    onClick(getItem(bindingAdapterPosition))
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: AlbumInfo) = with(binding) {
            albumItemInfo.text = "${item.musics.size} tracks"
            albumItemName.text = item.album.name
            Glide.with(root)
                .applyDefaultRequestOptions(Constants.glideDiskCacheStrategy)
                .load(Constants.ALBUM_ART_PATH + "/" + item.album.id)
                .error(R.drawable.music_player_icon) // TODO: change Unknown icon
                .into(albumItemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val binding = AlbumItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
