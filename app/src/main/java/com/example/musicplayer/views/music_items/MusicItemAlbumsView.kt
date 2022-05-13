package com.example.musicplayer.views.music_items

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemAlbumsBinding
import com.example.musicplayer.utils.secondToTimeFormatter
import com.example.musicplayer.utils.setup

class MusicItemAlbumsView  : MusicItemView {

    constructor(
        context: Context
    ) : super(context, R.layout.music_item_albums)

    constructor(
        context: Context,
        attr: AttributeSet
    ) : super(context, attr, R.layout.music_item_albums)

    private lateinit var binding: MusicItemAlbumsBinding

    override fun onViewCreated(view: View) {
        binding = MusicItemAlbumsBinding.bind(view).apply {
            musicItemSelect.setup {}
        }
    }

    override fun changeSelectionState(isActive: Boolean) = with(binding) {
        musicItemSelect.isVisible = isActive
        musicItemMore.isVisible = isActive.not()
    }

    fun setMusic(music: Music) = with(binding) {
        musicItemName.text = music.name
        musicItemTime.text = music.time.secondToTimeFormatter()
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