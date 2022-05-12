package com.example.musicplayer.data.repository

import androidx.paging.DataSource
import com.example.musicplayer.data.local.LocalPagingDataSource
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.di.annotations.DispatcherIO
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MusicLocalPagingRepository @Inject constructor(
    private val dataSource: LocalPagingDataSource,
    @DispatcherIO private val dispatcher: CoroutineContext
) {

    fun getTracks(): DataSource.Factory<Int, Music> {
        return dataSource.getTracks()
    }

    fun getAlbums(albumId: Long): DataSource.Factory<Int, Music> {
        return dataSource.getAlbums(albumId)
    }

    fun getArtists(artistId: Long): DataSource.Factory<Int, Music> {
        return dataSource.getArtists(artistId)
    }

    fun getFavorites(): DataSource.Factory<Int, Music> {
        return dataSource.getFavorites()
    }
}