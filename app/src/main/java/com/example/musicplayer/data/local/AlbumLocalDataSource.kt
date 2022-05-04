package com.example.musicplayer.data.local

import com.example.musicplayer.data.local.db.AlbumDao
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.di.annotations.DispatcherIO
import com.example.musicplayer.ui.model.AlbumInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AlbumLocalDataSource @Inject constructor(
    private val dao: AlbumDao,
    @DispatcherIO private val dispatcher: CoroutineContext,
) : ILocalDataSource<Album, Long>(dao) {
    override fun <T> search(query: T): Flow<Album>? {
        return null
    }

    fun getAlbumsInfo(): Flow<List<AlbumInfo>> {
        return dao.getAlbumsInfo()
    }
}
