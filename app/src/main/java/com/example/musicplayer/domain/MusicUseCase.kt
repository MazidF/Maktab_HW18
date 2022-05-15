package com.example.musicplayer.domain

import android.content.Context
import androidx.paging.DataSource
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.data.model.AlbumInfo
import com.example.musicplayer.utils.toMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MusicUseCase (
    private val repository: MusicRepository,
    private val dispatcher: CoroutineContext = Dispatchers.IO, // TODO: search about domain layer dispatcher
    context: Context? = null
) {
    private var hasBeenLoaded: Boolean = context == null
    private val scope = CoroutineScope(dispatcher)

    val musicStateFlow = MutableStateFlow<List<Music>>(emptyList())
    val albumStateFlow = MutableStateFlow<List<Album>>(emptyList())
    val artistStateFlow = MutableStateFlow<List<Artist>>(emptyList())

    val albumMapStateFlow = MutableStateFlow<HashMap<Long, Album>>(hashMapOf())
    val artistMapStateFlow = MutableStateFlow<HashMap<Long, Artist>>(hashMapOf())

    init {
        scope.launch {
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
                    albumMapStateFlow.emit(it.toMap { key ->  key.id })
                }
            }
            launch {
                repository.getAllArtists().collect {
                    artistStateFlow.emit(it)
                    artistMapStateFlow.emit(it.toMap { key ->  key.id })
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
            MusicLists.FAVORITES -> {
                repository.getFavoriteMusics()
            }
            MusicLists.TRACKS -> {
                flow {
                    musicStateFlow.collect {
                        emit(it)
                    }
                }.flowOn(dispatcher)
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
}