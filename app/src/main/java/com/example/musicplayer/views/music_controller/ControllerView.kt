package com.example.musicplayer.views.music_controller

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.ControllerBinding
import com.example.musicplayer.domain.controller.MusicManager
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ControllerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet
) : MaterialCardView(context, attrs) {
    private val binding: ControllerBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private var isPlaying: Boolean? = null

    init {
        val root = inflate(context, R.layout.controller, this)
        binding = ControllerBinding.bind(root)
        binding.controllerMusicName.isSelected = true
        binding.controllerMusicArtist.isSelected = true
    }

    fun setMusic(music: Music, artist: String) = with(binding) {
        controllerMusicName.text = music.name
        controllerMusicArtist.text = artist
        scope.launch {
            val image = music.getAlbumImage()
            withContext(Dispatchers.Main) {
                Glide.with(root)
                    .load(image)
                    .error(R.drawable.music_player_icon)
                    .into(controllerImage)
            }
        }
    }

    fun setIsPlaying(isPlaying: Boolean) {
        if (isPlaying != this.isPlaying) {
            binding.controllerPausePlay.setImageResource(
                if (isPlaying.not()) R.drawable.ic_play else R.drawable.ic_pause
            )
        }
        this.isPlaying = isPlaying
    }

    private fun setBackgroundTheme() {
        TODO("Not yet implemented")
    }

    fun setOnMusicListClickListener(block: (View) -> Unit) {
        binding.controllerCurrentList.setOnClickListener(block)
    }

    fun setOnNextClickListener(block: (View) -> Unit) {
        binding.controllerNext.setOnClickListener(block)
    }

    fun setOnPrevClickListener(block: (View) -> Unit) {
        binding.controllerPrev.setOnClickListener(block)
    }

    fun setOnPausePlayClickListener(block: (View) -> Unit) {
        binding.controllerPausePlay.setOnClickListener(block)
    }
}