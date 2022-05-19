package com.example.musicplayer.data.repository

import android.content.Context
import androidx.paging.DataSource
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.data.model.AlbumInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext


// TODO: add dispatcher
class MusicRepository(
    private val local: MusicLocalRepository,
    private val localPaging: MusicLocalPagingRepository,
    private val remote: MusicRemoteRepository,
    private val dispatcher: CoroutineContext = Dispatchers.IO,
) {

    fun getAllMusics(): Flow<List<Music>> {
        return local.getAllMusics().flowOn(dispatcher)
    }

    fun getAllAlbums(): Flow<List<Album>> {
        return local.getAllAlbums()
    }

    fun getAllArtists(): Flow<List<Artist>> {
        return local.getAllArtists()
    }

    fun getAlbumsInfo(): Flow<List<AlbumInfo>> {
        return local.getAlbumsInfo()
    }

    suspend fun getMusicByAlbum(albumId: Long): Flow<List<Music>> {
        val album = local.getAlbum(albumId) ?: return flow {  }
        return local.getMusicByAlbum(album)
    }

    suspend fun getMusicByArtist(artistId: Long): Flow<List<Music>> {
        val artist = local.getArtist(artistId) ?: return flow {  }
        return local.getMusicByArtist(artist)
    }

    fun getFavoriteMusics(): Flow<List<Music>> {
        return local.getFavoriteMusics()
    }

    fun getTracksPaging(): DataSource.Factory<Int, Music> {
        return localPaging.getTracks()
    }

    fun getAlbumsPaging(albumId: Long): DataSource.Factory<Int, Music> {
        return localPaging.getAlbums(albumId)
    }

    fun getArtistsPaging(artistId: Long): DataSource.Factory<Int, Music> {
        return localPaging.getArtists(artistId)
    }

    fun getFavoritesPaging(): DataSource.Factory<Int, Music> {
        return localPaging.getFavorites()
    }

    suspend fun updateMusicItems(vararg musics: Music): Int {
        return local.updateMusicItems(*musics)
    }
}