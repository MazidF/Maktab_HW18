package com.example.musicplayer.domain

import androidx.paging.DataSource
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.AlbumInfo
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.utils.toMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext as CoroutineContext1

class MusicUseCase (
    private val repository: MusicRepository,
    private val dispatcher: CoroutineContext1 = Dispatchers.IO,
    // TODO: search about domain layer dispatcher
) {
    private val scope = CoroutineScope(dispatcher)

    // TODO: remove if needed
    private val musicStateFlow = MutableStateFlow<List<Music>>(emptyList())
    private val albumStateFlow = MutableStateFlow<List<Album>>(emptyList())
    private val artistStateFlow = MutableStateFlow<List<Artist>>(emptyList())

    ////////////////////////////////service_output//////////////////////////////////////

    val currentMusicStateFlow = MutableStateFlow<Music>(Music.empty)
    val currentMusicIsPlayingStateFlow = MutableStateFlow<Boolean>(false)
    val currentMusicDurationStateFlow = MutableStateFlow<Int>(0)
    val currentMusicHasShuffleStateFlow = MutableStateFlow<Boolean>(false)

    val albumMapStateFlow = MutableStateFlow<HashMap<Long, Album>>(hashMapOf())
    val artistMapStateFlow = MutableStateFlow<HashMap<Long, Artist>>(hashMapOf())

    init {
        scope.launch {
            launch {
                repository.getAllMusics().collect {
                    val bool = it.equals(musicStateFlow.value)
                    musicStateFlow.emit(it + listOf())
                }
            }
            launch {
                repository.getAllAlbums().collect {
                    albumStateFlow.emit(it)
                    albumMapStateFlow.emit(it.toMap { key ->  key.id })
                }
            }
            launch {
                repository.getAllArtists().collect {
                    artistStateFlow.emit(it)
                    artistMapStateFlow.emit(it.toMap { key ->  key.id })
                }
            }
            launch {
                musicStateFlow.collect {
                    val targetId = currentMusicStateFlow.value.id
                    it.firstOrNull { music ->
                        targetId == music.id
                    }?.let { music ->
                        updateMusic(music)
                    }
                }
            }
        }
    }

    fun getAlbumsInfo(): Flow<List<AlbumInfo>> {
        return repository.getAlbumsInfo()
    }

    suspend fun getMusicListFrom(musicLists: MusicLists): Flow<List<Music>> {
        return when(musicLists) {
            is MusicLists.ALBUMS -> {
                repository.getMusicByAlbum(musicLists.albumId)
            }
            is MusicLists.ARTISTS -> {
                repository.getMusicByArtist(musicLists.artistId)
            }
            is MusicLists.FAVORITES -> {
                repository.getFavoriteMusics()
            }
            is MusicLists.TRACKS -> {
                repository.getAllMusics()
            }
        }
    }

    fun getTracksPaging(): DataSource.Factory<Int, Music> {
        return repository.getTracksPaging()
    }

    fun getAlbumsPaging(albumId: Long): DataSource.Factory<Int, Music> {
        return repository.getAlbumsPaging(albumId)
    }

    fun getArtistsPaging(artistId: Long): DataSource.Factory<Int, Music> {
        return repository.getArtistsPaging(artistId)
    }

    fun getFavoritesPaging(): DataSource.Factory<Int, Music> {
        return repository.getFavoritesPaging()
    }

    //////////////////// music_state ////////////////////////

    suspend fun updateMusic(music: Music) {
        currentMusicStateFlow.emit(music)
    }

    suspend fun updateMusicIsPlaying(isPlaying: Boolean) {
        currentMusicIsPlayingStateFlow.emit(isPlaying)
    }

    suspend fun updateMusicDuration(duration: Int) {
        currentMusicDurationStateFlow.emit(duration)
    }

    suspend fun updateMusicShuffle(hasShuffle: Boolean) {
        currentMusicHasShuffleStateFlow.emit(hasShuffle)
    }

    suspend fun updateMusicItem(vararg musics: Music): Int {
        return repository.updateMusicItems(*musics)
    }
}