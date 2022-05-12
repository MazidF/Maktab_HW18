package com.example.musicplayer.data.local.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.example.musicplayer.data.model.Music

@Dao
interface PagingDao {

    @Query("select * from music_table")
    fun getTracks(): DataSource.Factory<Int, Music>

    @Query("select * from music_table where music_album_id = :albumId")
    fun getAlbums(
        albumId: Long
    ): DataSource.Factory<Int, Music>

    @Query("select * from music_table where music_artist_id = :artistId")
    fun getArtists(
        artistId: Long
    ): DataSource.Factory<Int, Music>

    @Query("select * from music_table where music_is_liked")
    fun getFavorites(): DataSource.Factory<Int, Music>
}
