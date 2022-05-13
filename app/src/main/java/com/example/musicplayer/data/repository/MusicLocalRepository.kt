package com.example.musicplayer.data.repository

import com.example.musicplayer.data.local.AlbumLocalDataSource
import com.example.musicplayer.data.local.ArtistLocalDataSource
import com.example.musicplayer.data.local.MusicLocalDataSource
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.AlbumInfo
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.di.annotations.DispatcherIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


@Singleton
class MusicLocalRepository @Inject constructor(
    private val musicDataSource: MusicLocalDataSource,
    private val albumDataSource: AlbumLocalDataSource,
    private val artistDataSource: ArtistLocalDataSource,
    @DispatcherIO private val dispatcher: CoroutineContext,
) {
    private val scope = CoroutineScope(dispatcher)

    fun loadMusics(flow: Flow<List<Music>>) {
        scope.launch {
            flow.collect {
                musicDataSource.insertItems(*it.toTypedArray())
            }
        }
    }

    fun loadAlbums(artistSet: HashSet<Album>) {
        scope.launch {
            albumDataSource.insertItems(*artistSet.toTypedArray())
        }
    }

    fun loadArtists(artistSet: HashSet<Artist>) {
        scope.launch {
            artistDataSource.insertItems(*artistSet.toTypedArray())
        }
    }

    fun getAllMusics(): Flow<List<Music>> {
        return musicDataSource.getItems().flowOn(dispatcher)
    }

    fun getAllAlbums(): Flow<List<Album>> {
        return albumDataSource.getItems().flowOn(dispatcher)
    }

    fun getAllArtists(): Flow<List<Artist>> {
        return artistDataSource.getItems().flowOn(dispatcher)
    }

    fun getAlbumsInfo(): Flow<List<AlbumInfo>> {
        return albumDataSource.getAlbumsInfo().flowOn(dispatcher)
    }

    fun getMusicByAlbum(album: Album): Flow<List<Music>> {
        return musicDataSource.search(album) ?: flow { }
    }

    fun getMusicByArtist(artist: Artist): Flow<List<Music>> {
        return musicDataSource.search(artist) ?: flow { }
    }

    suspend fun getAlbum(albumId: Long): Album? {
        return albumDataSource.get(albumId)
    }

    suspend fun getArtist(artistId: Long): Artist? {
        return artistDataSource.get(artistId)
    }

    fun getFavoriteMusics(): Flow<List<Music>> {
        return musicDataSource.favorites()
    }

}
