package com.example.musicplayer.ui.activity.main

import com.example.musicplayer.data.model.Music

class MusicHandler {
    lateinit var music: Music
    var isPlaying = false

    fun hasBeenSetup() = this::music.isInitialized

    fun getImagePath() {

    }

    fun changeMusic(music: Music) {
        this.music = music
    }
}
