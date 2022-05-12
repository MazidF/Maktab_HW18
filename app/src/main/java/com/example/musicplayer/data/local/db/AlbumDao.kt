package com.example.musicplayer.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.AlbumInfo
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlbumDao : IDao<Album, Long>(Album.TABLE_NAME) {
    @Query("select * from album_table where album_id = :primaryKey limit 1")
    abstract override fun get(primaryKey: Long): Flow<Album?>

    @Query("select * from album_table")
    abstract override fun getItems(): Flow<List<Album>>

    @RawQuery(observedEntities = [Album::class])
    abstract override fun search(query: SupportSQLiteQuery): Flow<List<Album>>

    @Query("select count(*) from album_table")
    abstract override suspend fun getCount(): Int

    @Query("select * from album_table")
    abstract fun getAlbumsInfo(): Flow<List<AlbumInfo>>
}

