package com.example.musicplayer.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicplayer.data.local.db.AlbumDao
import com.example.musicplayer.data.local.db.ArtistDao
import com.example.musicplayer.data.local.db.MusicDao
import com.example.musicplayer.data.local.db.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "music_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMusicDao(
        database: MusicDatabase
    ): MusicDao {
        return database.musicDao()
    }

    @Provides
    @Singleton
    fun provideAlbumDao(
        database: MusicDatabase
    ): AlbumDao {
        return database.albumDao()
    }

    @Provides
    @Singleton
    fun provideArtistDao(
        database: MusicDatabase
    ): ArtistDao {
        return database.artistDao()
    }

}