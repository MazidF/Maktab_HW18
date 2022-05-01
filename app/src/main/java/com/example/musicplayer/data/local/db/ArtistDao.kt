package com.example.musicplayer.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.musicplayer.data.local.IDao
import com.example.musicplayer.data.model.Artist
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistDao : IDao<Artist, Long>(Artist.TABLE_NAME) {
    @Query("select * from artist_table where artist_id = :primaryKey limit 1")
    abstract override fun get(primaryKey: Long): Flow<Artist?>

    @Query("select * from artist_table")
    abstract override fun getItems(): Flow<List<Artist>>

    @RawQuery(observedEntities = [Artist::class])
    abstract override fun search(query: SupportSQLiteQuery): Flow<Artist>

    @Query("select count(*) from artist_table")
    abstract override suspend fun getCount(): Int
}