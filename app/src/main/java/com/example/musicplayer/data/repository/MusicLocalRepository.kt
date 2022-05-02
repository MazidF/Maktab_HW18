package com.example.musicplayer.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.example.musicplayer.data.local.AlbumLocalDataSource
import com.example.musicplayer.data.local.ArtistLocalDataSource
import com.example.musicplayer.data.local.MusicLocalDataSource
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.di.annotations.DispatcherIO
import com.example.musicplayer.utils.*
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


// TODO: add dispatcher
class MusicLocalRepository @Inject constructor(
    private val musicDataSource: MusicLocalDataSource,
    private val albumDataSource: AlbumLocalDataSource,
    private val artistDataSource: ArtistLocalDataSource,
    @DispatcherIO private val dispatcher: CoroutineContext,
) {

    private fun loadMusics(context: Context, uri: Uri): Flow<Music> = flow {
        val projection = arrayOf(
            MediaInfo.ALBUM,
            MediaInfo.ALBUM_ID,
            MediaInfo.ARTIST,
            MediaInfo.ARTIST_ID,
            MediaInfo.DATA,
            MediaInfo.DISPLAY_NAME,
            MediaInfo.DURATION
        ) // or null to be easier :)
        val selection = MediaInfo.IS_MUSIC + " != 0"
        val sortOrder = MediaInfo.DISPLAY_NAME + " ASC"
        val cursor: Cursor = context.contentResolver
            .query(uri, projection, selection, null, sortOrder, ) ?: return@flow

        val albums = albumDataSource.getItems().first().toMap { name }
        val artists = artistDataSource.getItems().first().toMap { name }

        val setArtist = hashSetOf<Artist>()
        val setAlbum = hashSetOf<Album>()

        val nameColumn = cursor.getColumnIndex(MediaInfo.DISPLAY_NAME)
        val dataColumn = cursor.getColumnIndex(MediaInfo.DATA)
        val albumColumn = cursor.getColumnIndex(MediaInfo.ALBUM)
        val artistColumn = cursor.getColumnIndex(MediaInfo.ARTIST)
        val albumIdColumn = cursor.getColumnIndex(MediaInfo.ALBUM_ID)
        val artistIdColumn = cursor.getColumnIndex(MediaInfo.ARTIST_ID)
        val durationColumn = cursor.getColumnIndex(MediaInfo.DURATION)

        var songName: String
        var path: String
        var albumName: String
        var artistName: String
        var albumId: Long
        var artistId: Long
        var time: Int

        if (cursor.moveToFirst()) {
            do {
                songName = cursor.getString(nameColumn)

                path = cursor.getString(dataColumn)

                albumName = cursor.getString(albumColumn)

                artistName = cursor.getString(artistColumn)

                albumId = albums[albumName]?.id ?: cursor.getLong(albumIdColumn).also {
                    setAlbum.add(Album(it, albumName))
                }

                artistId = artists[artistName]?.id ?: cursor.getLong(artistIdColumn).also {
                    setArtist.add(Artist(it, artistName))
                }

                time = cursor.getInt(durationColumn).run {
                    if (this < 0)
                        0
                    else
                        cursor.getInt(this)
                }

                emit(
                    Music(
                        name = songName,
                        time = (time / 1000).timeFormatter(),
                        data = path,
                        artistId = artistId,
                        albumId = albumId
                    ).also {
                        musicDataSource.insertItems(it)
                    }
                )

            } while (cursor.moveToNext())
        }
        albumDataSource.insertItems(*setAlbum.toTypedArray())
        artistDataSource.insertItems(*setArtist.toTypedArray())

        cursor.close()
    }.flowOn(dispatcher)

    fun loadMusics(context: Context): Flow<List<Music>> {
        val uri = MediaInfo.EXTERNAL_CONTENT_URI
        return loadMusics(context, uri).collectAsList(10)
    }

    fun getAllMusics(): Flow<List<Music>> {
        return musicDataSource.getItems()
    }

    fun getAllAlbums(): Flow<List<Album>> {
        return albumDataSource.getItems()
    }

    fun getAllArtists(): Flow<List<Artist>> {
        return artistDataSource.getItems()
    }

}
