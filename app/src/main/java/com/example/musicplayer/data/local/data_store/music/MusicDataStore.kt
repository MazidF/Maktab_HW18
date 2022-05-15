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
        val musicList = preference[MusicPreferencesKey.MUSIC_LIST_KEY]?.let { className ->
            when(className) {
                MusicLists.ALBUMS::class.java.simpleName -> {
                    val albumId = preference[MusicPreferencesKey.MUSIC_ALBUM_ID_KEY]!!
                    MusicLists.ALBUMS(albumId)
                }
                MusicLists.ARTISTS::class.java.simpleName -> {
                    val artistId = preference[MusicPreferencesKey.MUSIC_ARTIST_ID_KEY]!!
                    MusicLists.ARTISTS(artistId)
                }
                MusicLists.FAVORITES.name() -> MusicLists.FAVORITES
                MusicLists.TRACKS.name() -> MusicLists.TRACKS
                else -> {
                    throw Exception("Invalid ClassName for MusicLists.")
                }
            }
        } ?: MusicLists.TRACKS
        val musicIndex = preference[MusicPreferencesKey.MUSIC_INDEX_KEY] ?: 0
        val hasShuffle = preference[MusicPreferencesKey.MUSIC_HAS_SHUFFLE_KEY] ?: false
        val musicState = MusicState(
            musicPosition = preference[MusicPreferencesKey.MUSIC_POSITION_KEY] ?: 0,
            musicIsPlaying = preference[MusicPreferencesKey.MUSIC_IS_PLAYING_KEY] ?: false,
        )
        MusicPreferenceInfo(
            musicList = musicList,
            musicIndex = musicIndex,
            hasShuffle = hasShuffle,
            musicState = musicState,
        )
    }.flowOn(dispatcher)

    // TODO: remove id
    // id is for albums or artists list.
    suspend fun updateMusicList(musicLists: MusicLists, id: Long?) {
        datastore.edit {
            it[MusicPreferencesKey.MUSIC_LIST_KEY]  = musicLists::class.java.simpleName
            when(musicLists) {
                is MusicLists.ALBUMS -> {
                    it[MusicPreferencesKey.MUSIC_ALBUM_ID_KEY] = id!!
                }
                is MusicLists.ARTISTS -> {
                    it[MusicPreferencesKey.MUSIC_ARTIST_ID_KEY] = id!!
                }
            }
        }
    }

    suspend fun updateMusicIndex(index: Int) {
        datastore.edit {
            it[MusicPreferencesKey.MUSIC_INDEX_KEY] = index
        }
    }

    suspend fun updateHasShuffle(hasShuffle: Boolean) {
        datastore.edit {
            it[MusicPreferencesKey.MUSIC_HAS_SHUFFLE_KEY] = hasShuffle
        }
    }

    suspend fun updateMusicState(musicState: MusicState) {
        datastore.edit {
            it[MusicPreferencesKey.MUSIC_POSITION_KEY] = musicState.musicPosition
            it[MusicPreferencesKey.MUSIC_IS_PLAYING_KEY] = musicState.musicIsPlaying
        }
    }

    private object MusicPreferencesKey {
        val MUSIC_LIST_KEY = stringPreferencesKey("musicListKey")
        val MUSIC_INDEX_KEY = intPreferencesKey("musicIndexKey")
        val MUSIC_ALBUM_ID_KEY = longPreferencesKey("musicAlbumIdKey")
        val MUSIC_ARTIST_ID_KEY = longPreferencesKey("musicArtistIdKey")
        val MUSIC_HAS_SHUFFLE_KEY = booleanPreferencesKey("musicHasShuffleKey")
        val MUSIC_POSITION_KEY = intPreferencesKey("musicPositionKey")
        val MUSIC_IS_PLAYING_KEY = booleanPreferencesKey("musicIsPlayingKey")
    }
}