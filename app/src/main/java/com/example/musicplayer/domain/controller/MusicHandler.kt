package com.example.musicplayer.domain.controller

import com.example.musicplayer.data.model.Music

class MusicHandler {
    lateinit var music: Music
    var isPlaying = false

    fun hasBeenSetup() = this::music.isInitialized

    fun changeMusic(music: Music) {
        this.music = music
    }
}