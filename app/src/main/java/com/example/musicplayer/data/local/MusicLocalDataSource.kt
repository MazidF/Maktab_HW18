package com.example.musicplayer.data.local

import androidx.paging.DataSource
import com.example.musicplayer.data.local.db.MusicDao
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.di.annotations.DispatcherIO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MusicLocalDataSource @Inject constructor(
    private val dao: MusicDao,
    @DispatcherIO private val dispatcher: CoroutineContext,
) : ILocalDataSource<Music, Long>(dao) {

    override fun <T> search(query: T): Flow<Music>? {
        return when (query) {
            is String -> {
                dao.searchByName(query)
            }
            is Album -> {
                dao.searchByAlbum(query.id)
            }
            is Artist -> {
                dao.searchByArtist(query.id)
            }
            else -> null
        }
    }

    fun getItemsPaging(): DataSource.Factory<Long, Music> {
        return dao.getItemsPaging()
    }
}