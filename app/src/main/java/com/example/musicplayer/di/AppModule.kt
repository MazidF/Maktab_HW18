package com.example.musicplayer.di

import android.content.Context
import com.example.musicplayer.data.repository.MusicLocalPagingRepository
import com.example.musicplayer.data.repository.MusicLocalRepository
import com.example.musicplayer.data.repository.MusicRemoteRepository
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.di.annotations.DispatcherDefault
import com.example.musicplayer.di.annotations.DispatcherIO
import com.example.musicplayer.di.annotations.DispatcherMain
import com.example.musicplayer.di.annotations.HasBeenLoaded
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.domain.controller.MusicManager
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.hasBeenLoaded
import com.example.musicplayer.utils.sharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        local: MusicLocalRepository,
        localPaging: MusicLocalPagingRepository,
        remote: MusicRemoteRepository,
    ): MusicRepository {
        return MusicRepository(
            local = local,
            localPaging = localPaging,
            remote = remote,
        )
    }

    @Provides
    @Singleton
    fun provideMusicUseCase(
        repository: MusicRepository,
        @DispatcherIO dispatcher: CoroutineContext,
        @ApplicationContext context: Context
    ): MusicUseCase {
        return MusicUseCase(
            repository = repository,
            dispatcher = dispatcher,
        )
    }

    @Provides
    @Singleton
    @HasBeenLoaded
    fun provideHasLoaded(
        @ApplicationContext context: Context
    ): Boolean {
        val sharedPreferences = context.sharedPreferences()
        return sharedPreferences.getBoolean(Constants.HAS_BEEN_LOADED, false)
    }

    @Provides
    @Singleton
    fun provideMusicManager() : MusicManager {
        return MusicManager()
    }

    @Provides
    @Singleton
    @DispatcherMain
    fun provideDispatcherMain() : CoroutineContext {
        return Dispatchers.Main
    }

    @Provides
    @Singleton
    @DispatcherIO
    fun provideDispatcherIO() : CoroutineContext {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    @DispatcherDefault
    fun provideDispatcherDefault() : CoroutineContext {
        return Dispatchers.Default
    }

}