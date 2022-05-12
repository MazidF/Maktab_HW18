package com.example.musicplayer.data.local

import androidx.paging.DataSource
import com.example.musicplayer.data.local.db.PagingDao
import com.example.musicplayer.data.model.Music
import javax.inject.Inject

class LocalPagingDataSource @Inject constructor(
    private val dao: PagingDao
) {

    fun getTracks(): DataSource.Factory<Int, Music> {
        return dao.getTracks()
    }

    fun getAlbums(albumId: Long): DataSource.Factory<Int, Music> {
        return dao.getAlbums(albumId)
    }

    fun getArtists(artistId: Long): DataSource.Factory<Int, Music> {
        return dao.getArtists(artistId)
    }

    fun getFavorites(): DataSource.Factory<Int, Music> {
        return dao.getFavorites()
    }
}