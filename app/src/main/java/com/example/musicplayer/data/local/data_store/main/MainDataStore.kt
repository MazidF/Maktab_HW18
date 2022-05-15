package com.example.musicplayer.data.local.data_store.main

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.musicplayer.di.annotations.DispatcherIO
import com.example.musicplayer.di.annotations.HasBeenLoaded
import com.example.musicplayer.utils.logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

private const val DATA_STORE_NAME = "main_datastore"
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

@Singleton
class MainDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIO dispatcher: CoroutineContext
) {
    private val dataStore = context.datastore

    val preferences = dataStore.data.catch { cause ->
        logger(cause.message.toString())
    }.map { preference ->
        val hasBeenLoaded = preference[MainPreferencesKey.MUSIC_HAS_LOADED] ?: false
        MainInfo(
            musicHasBeenLoaded = hasBeenLoaded,
        )
    }.flowOn(dispatcher)

    suspend fun updateHasBeenLoaded(hasBeenLoaded: Boolean) {
        dataStore.edit {
            it[MainPreferencesKey.MUSIC_HAS_LOADED] = hasBeenLoaded
        }
    }

    private object MainPreferencesKey {
        val MUSIC_HAS_LOADED = booleanPreferencesKey("music_has_loaded")
    }
}
