package com.example.musicplayer.data.local.data_store.music

data class MusicState(
    val musicPosition: Int,
    val musicIsPlaying: Boolean,
) {
    companion object {
        val empty = MusicState(
            musicPosition = -1,
            musicIsPlaying = false,
        )
    }
}