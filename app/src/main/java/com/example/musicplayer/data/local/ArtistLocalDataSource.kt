package com.example.musicplayer.data.local

import com.example.musicplayer.data.local.db.ArtistDao
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.di.annotations.DispatcherIO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ArtistLocalDataSource @Inject constructor(
    private val dao: ArtistDao,
    @DispatcherIO private val dispatcher: CoroutineContext,
) : ILocalDataSource<Artist, Long>(dao) {
    override fun <T> search(query: T): Flow<Artist>? {
        return null
    }
}