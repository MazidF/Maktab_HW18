package com.example.musicplayer.data.repository

import android.content.Context
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.data.model.AlbumInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext


// TODO: add dispatcher
class MusicRepository(
    private val local: MusicLocalRepository,
    private val remote: MusicRemoteRepository,
    private val dispatcher: CoroutineContext = Dispatchers.IO,
) {

    fun loadMusics(context: Context): Flow<List<Music>> {
        return local.loadMusics(context)
    }

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

    fun getAllMusicsPaging(config: PagedList.Config): LivePagedListBuilder<Long, Music> {
        return local.getAllMusicPaging(config)
    }

    suspend fun getMusics(from: Int, parPage: Int): List<Music> {
        return local.getMusics(from, parPage)
    }
}