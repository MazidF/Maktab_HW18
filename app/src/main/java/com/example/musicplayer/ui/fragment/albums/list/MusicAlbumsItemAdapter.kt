package com.example.musicplayer.ui.fragment.albums.list

import android.view.ViewGroup
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.views.music_items.MusicItemTracksView

class MusicAlbumsItemAdapter(
    onItemClick: (Music) -> Unit,
) : MusicItemAdapter(onItemClick) {

    private inner class MusicAlbumHolder(
        private val view: MusicItemTracksView
    ) : MusicHolder(view) {

        init {
            with(view) {
                setOnMoreOptionClickedListener {

                }
                setOnSelectionChangedListener { isSelected ->

                }
            }
        }

        override fun bind(music: Music) = with(view) {

        }

        override fun select(isSelected: Boolean) {
            view.selected(isSelected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        return MusicAlbumHolder(MusicItemTracksView(parent.context))
    }
}