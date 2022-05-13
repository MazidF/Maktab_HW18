package com.example.musicplayer.views.music_items

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemTracksBinding
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.setup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicItemTracksView  : MusicItemView {

    constructor(
        context: Context
    ) : super(context, R.layout.music_item_tracks)

    constructor(
        context: Context,
        attr: AttributeSet
    ) : super(context, attr, R.layout.music_item_tracks)

    private lateinit var binding: MusicItemTracksBinding

    override fun onViewCreated(view: View) {
        binding = MusicItemTracksBinding.bind(view).apply {
            musicItemSelect.setup {}
        }
    }

    override fun changeSelectionState(isActive: Boolean) = with(binding) {
        musicItemSelect.isVisible = isActive
        musicItemMore.isVisible = isActive.not()
    }

    suspend fun setMusic(music: Music, artistName: String) = with(binding) {
        musicItemName.text = music.name
        musicItemArtist.text = artistName
        val icon = music.getAlbumImage()
        withContext(Dispatchers.Main) {
            Glide.with(root)
                .applyDefaultRequestOptions(Constants.glideDiskCacheStrategy)
                .load(icon)
                .error(R.drawable.music_player_icon)
                .into(musicItemImage)
        }
    }

    fun setOnSelectionChangedListener(block: (Boolean) -> Unit) {
        binding.musicItemSelect.setup {
            block(this.isSelected)
        }
    }

    fun setOnMoreOptionClickedListener(block: (View) -> Unit) {
        binding.musicItemMore.setOnClickListener(block)
    }
}