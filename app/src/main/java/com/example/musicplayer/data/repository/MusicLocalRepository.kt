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
    private fun Cursor.getString(name: String): String {
        val index = getColumnIndex(name)
        if (index < 0) return Constants.UNKNOWN
        return getString(index)
    }

    private fun Cursor.getLong(name: String): Long {
        val index = getColumnIndex(name)
        if (index < 0) return Constants.UNKNOWN_ID
        return getLong(index)
    }

    private fun loadMusics(context: Context, uri: Uri): Flow<Music> = flow {
//        val uri = MediaInfo.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaInfo.ALBUM,
            MediaInfo.ALBUM_ID,
            MediaInfo.ARTIST,
            MediaInfo.ARTIST_ID,
            MediaInfo.DATA,
            MediaInfo.DISPLAY_NAME,
            MediaInfo.DURATION
        )
        val selection = MediaInfo.IS_MUSIC + " != 0"
        val sortOrder = MediaInfo.DISPLAY_NAME + " ASC"
        val cursor: Cursor = context.contentResolver
            .query(uri, projection, selection, null, sortOrder, ) ?: return@flow

        val albums = albumDataSource.getItems().first().toMap { name }
        val artists = artistDataSource.getItems().first().toMap { name }

        val setArtist = hashSetOf<Artist>()
        val setAlbum = hashSetOf<Album>()

        if (cursor.moveToFirst()) {
            do {
                val songName: String = cursor.getString(MediaInfo.DISPLAY_NAME)

                val path: String = cursor.getString(MediaInfo.DATA)

                val albumName: String = cursor.getString(MediaInfo.ALBUM)

                val artistName: String = cursor.getString(MediaInfo.ARTIST)

                val albumId: Long = albums[albumName]?.id ?: cursor.getLong(MediaInfo.ALBUM_ID).also {
                    setAlbum.add(Album(it, albumName))
                }

                val artistId: Long = artists[artistName]?.id ?: cursor.getLong(MediaInfo.ARTIST_ID).also {
                    setArtist.add(Artist(it, artistName))
                }

                val time: Int = cursor.getColumnIndex(MediaInfo.DURATION).run {
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
        val file = File(uri.path)
        val a = file.listFiles { f, _ ->
            f.isDirectory
        }

        a?.forEach {
            loadMusics(context, it.toUri())
        }
    }.flowOn(dispatcher)

    fun getAllMusics(context: Context? = null): Flow<List<Music>> {
        if (context != null) {
            val uri = MediaInfo.EXTERNAL_CONTENT_URI
            return loadMusics(context, uri).collectAsList(10)
        }
        return musicDataSource.getItems()
    }

}
