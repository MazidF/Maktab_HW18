package com.example.musicplayer.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.musicplayer.data.local.db.*
import com.example.musicplayer.workmanager.LoadMusicWorker
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

    @Provides
    @Singleton
    fun providePagingDao(
        database: MusicDatabase
    ): PagingDao {
        return database.pagingDao()
    }

}