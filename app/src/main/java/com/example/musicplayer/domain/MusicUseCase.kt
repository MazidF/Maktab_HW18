package com.example.musicplayer.domain

import android.content.Context
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.di.annotations.HasBeenLoaded
import com.example.musicplayer.ui.model.AlbumInfo
import com.example.musicplayer.utils.StateFlowWrapper
import com.example.musicplayer.utils.loaded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MusicUseCase (
    private val repository: MusicRepository,
    private val dispatcher: CoroutineContext = Dispatchers.IO, // TODO: search about domain layer dispatcher
    context: Context? = null
) {
    private var hasBeenLoaded: Boolean = context == null

    init {
        CoroutineScope(dispatcher).launch {
            launch {
                repository.getAllMusics().collect {
                    if (hasBeenLoaded) {
                        musicStateFlow.emit(it)
                    }
                }
            }
            launch {
                repository.getAllAlbums().collect {
                    albumStateFlow.emit(it)
                }
            }
            launch {
                repository.getAllArtists().collect {
                    artistStateFlow.emit(it)
                }
            }
            context?.let {
                repository.loadMusics(it).collect { list ->
                    musicStateFlow.emit(musicStateFlow.value() + list)
                }
                hasBeenLoaded = true
                it.loaded()
            }
        }
    }

    val musicStateFlow = StateFlowWrapper<List<Music>>(emptyList())
    val albumStateFlow = StateFlowWrapper<List<Album>>(emptyList())
    val artistStateFlow = StateFlowWrapper<List<Artist>>(emptyList())

    fun getAlbumsInfo(): Flow<List<AlbumInfo>> {
        return repository.getAlbumsInfo()
    }
}