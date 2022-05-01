package com.example.musicplayer.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.musicplayer.data.local.IDao
import com.example.musicplayer.data.model.Music
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MusicDao : IDao<Music, Long>(Music.TABLE_NAME) {
    @Query("select * from music_table where music_id = :primaryKey limit 1")
    abstract override fun get(primaryKey: Long): Flow<Music?>

    @Query("select * from music_table")
    abstract override fun getItems(): Flow<List<Music>>

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
}