package com.example.musicplayer.domain

import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.utils.StateFlowWrapper
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class MusicUseCase (
    private val repository: MusicRepository,
    private val dispatcher: CoroutineContext = Dispatchers.IO // TODO: search about domain layer dispatcher
) {

    val musicWrapper = StateFlowWrapper<List<Music>>(emptyList())
    val albumWrapper = StateFlowWrapper<List<Album>>(emptyList())
    val artistWrapper = StateFlowWrapper<List<Artist>>(emptyList())

}