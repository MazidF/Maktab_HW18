package com.example.musicplayer.ui.fragment.tracks

import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionTracker
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.utils.*
import com.example.musicplayer.views.music_items.MusicItemTracksView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MusicTracksItemAdapter(
    private val artistList: HashMap<Long, Artist>,
    onItemClick: (Music) -> Unit,
) : MusicItemAdapter(onItemClick) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private inner class MusicTracksHolder(
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

        override fun bind(music: Music): Unit = with(view) {
            scope.launch {
                setMusic(music, artistList[music.artistId]?.name ?: "")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        return MusicTracksHolder(MusicItemTracksView(parent.context))
    }

    fun scrollToFirst(filter: (Music) -> Boolean, block: (Int) -> Unit) {
        val index = currentList?.indexOfFirst {
            filter(it)
        } ?: return
        block(index)
    }
}