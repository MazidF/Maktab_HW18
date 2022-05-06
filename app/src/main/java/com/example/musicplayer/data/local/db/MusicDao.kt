package com.example.musicplayer.data.local.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Music
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MusicDao : IDao<Music, Long>(Music.TABLE_NAME) {
    @Query("select * from music_table where music_id = :primaryKey limit 1")
    abstract override fun get(primaryKey: Long): Flow<Music?>

    @Query("select * from music_table order by music_name")
    abstract override fun getItems(): Flow<List<Music>>


    ///////////////////// paging /////////////////////////

    @Query("select * from music_table order by music_name")
    abstract fun getItemsPaging(): DataSource.Factory<Long, Music>

    ///////////////////// paging /////////////////////////

    @RawQuery(observedEntities = [Music::class])
    abstract override fun search(query: SupportSQLiteQuery): Flow<Music>

    @Query("select count(*) from music_table")
    abstract override suspend fun getCount(): Int

    fun searchByName(name: String): Flow<Music> {
        return search("LOWER(music_name) like ?", arrayOf(name.lowercase()))
    }

    fun searchByArtist(artistId: Long): Flow<Music> {
        return search("music_artist_id = ?", arrayOf(artistId))
    }

    fun searchByAlbum(albumId: Long): Flow<Music> {
        return search("music_album_id = ?", arrayOf(albumId))
    }

    @Query("select * from music_table limit :from, :perPage")
    abstract override suspend fun getItems(from: Int, perPage: Int): List<Music>
}