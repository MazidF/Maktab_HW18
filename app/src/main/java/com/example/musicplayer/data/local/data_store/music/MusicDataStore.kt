package com.example.musicplayer.data.local.data_store.music

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.musicplayer.di.annotations.DispatcherIO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

private const val DATA_STORE_NAME = "music_datastore"

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

@Singleton
class MusicDataStore @Inject constructor(
    @ApplicationContext context: Context,
    @DispatcherIO dispatcher: CoroutineContext
) {
    private val datastore = context.datastore
    val preferences: Flow<MusicPreferenceInfo> = datastore.data.catch {
        // TODO: no idea for this part.
    }.map { preference ->
        val musicList: MusicLists = preference[MusicPreferencesKey.CURRENT_MUSIC_LIST_KEY]?.let { className ->
            when(className) {
                MusicLists.ALBUMS::class.java.simpleName -> {
                    val albumId = preference[MusicPreferencesKey.CURRENT_MUSIC_ALBUM_ID_KEY]!!
                    MusicLists.ALBUMS(albumId)
                }
                MusicLists.ARTISTS::class.java.simpleName -> {
                    val artistId = preference[MusicPreferencesKey.CURRENT_MUSIC_ARTIST_ID_KEY]!!
                    MusicLists.ARTISTS(artistId)
                }
                MusicLists.FAVORITES.name() -> MusicLists.FAVORITES
                MusicLists.TRACKS.name() -> MusicLists.TRACKS
                else -> {
                    throw Exception("Invalid ClassName for MusicLists.")
                }
            }
        } ?: MusicLists.TRACKS
        val musicIndex = preference[MusicPreferencesKey.CURRENT_MUSIC_INDEX_KEY] ?: 0
        val hasShuffle = preference[MusicPreferencesKey.CURRENT_MUSIC_HAS_SHUFFLE_KEY] ?: false
        MusicPreferenceInfo(
            musicList = musicList,
            musicIndex = musicIndex,
            hasShuffle = hasShuffle,
        )
    }.flowOn(dispatcher)

    // id is for albums or artists list.
    suspend fun updateMusicList(musicLists: MusicLists, id: Int?) {
        datastore.edit {
            it[MusicPreferencesKey.CURRENT_MUSIC_LIST_KEY]  = musicLists::class.java.simpleName
            when(musicLists) {
                is MusicLists.ALBUMS -> {
                    it[MusicPreferencesKey.CURRENT_MUSIC_ALBUM_ID_KEY] = id!!
                }
                is MusicLists.ARTISTS -> {
                    it[MusicPreferencesKey.CURRENT_MUSIC_ARTIST_ID_KEY] = id!!
                }
            }
        }
    }

    suspend fun updateMusicIndex(index: Int) {
        datastore.edit {
            it[MusicPreferencesKey.CURRENT_MUSIC_INDEX_KEY] = index
        }
    }

    suspend fun updateHasShuffle(hasShuffle: Boolean) {
        datastore.edit {
            it[MusicPreferencesKey.CURRENT_MUSIC_HAS_SHUFFLE_KEY] = hasShuffle
        }
    }

    private object MusicPreferencesKey {
        val CURRENT_MUSIC_LIST_KEY = stringPreferencesKey("currentMusicListKey")
        val CURRENT_MUSIC_INDEX_KEY = intPreferencesKey("currentMusicIndexKey")
        val CURRENT_MUSIC_ALBUM_ID_KEY = intPreferencesKey("currentMusicAlbumIdKey")
        val CURRENT_MUSIC_ARTIST_ID_KEY = intPreferencesKey("currentMusicArtistIdKey")
        val CURRENT_MUSIC_HAS_SHUFFLE_KEY = booleanPreferencesKey("currentMusicHasShuffleKey")
    }
}