package com.example.musicplayer.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.utils.Converters

@Database(
    entities = [Music::class, Artist::class, Album::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun pagingDao(): PagingDao
}