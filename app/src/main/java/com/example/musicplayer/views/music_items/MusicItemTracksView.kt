package com.example.musicplayer.views.music_items

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.example.musicplayer.R
import com.example.musicplayer.databinding.MusicItemTracksBinding
import com.example.musicplayer.utils.set
import com.example.musicplayer.utils.setup

class MusicItemTracksView  : MusicItemView {

    constructor(
        context: Context
    ) : super(context, R.layout.music_item_tracks)

    constructor(
        context: Context,
        attr: AttributeSet
    ) : super(context, attr, R.layout.music_item_tracks)

    lateinit var binding: MusicItemTracksBinding

    override fun onViewCreated(view: View) {
        binding = MusicItemTracksBinding.bind(view)
    }

    override fun changeSelectionState(isActive: Boolean) = with(binding) {
        musicItemSelect.isVisible = isActive
        musicItemMore.isVisible = isActive.not()
    }

    override fun selected(isSelected: Boolean) = with(binding) {
        musicItemSelect.set(isSelected)
    }

/*    suspend fun setMusic(music: Music, artistName: String) = with(binding) {
        musicItemName.text = music.name
        musicItemArtist.text = artistName
        val icon = music.getAlbumImage()
        (Dispatchers.Main) {
            Glide.with(root)
                .applyDefaultRequestOptions(Constants.glideDiskCacheStrategy)
                .load(icon)
                .error(R.drawable.music_player_icon)
                .into(musicItemImage)
        }
    }*/

    fun setOnSelectionChangedListener(block: (Boolean) -> Unit) {
        binding.musicItemSelect.setup {
            block(this.isSelected)
        }
    }

    fun setOnMoreOptionClickedListener(block: (View) -> Unit) {
        binding.musicItemMore.setOnClickListener(block)
    }
}