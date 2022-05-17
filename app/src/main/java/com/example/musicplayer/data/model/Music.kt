package com.example.musicplayer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.musicplayer.R
import com.example.musicplayer.utils.Constants.MUSIC_PER_PAGE
import com.example.musicplayer.utils.Constants.musicBitmaps
import com.example.musicplayer.utils.pathToBitmap

@Entity(
    tableName = Music.TABLE_NAME,
    indices = [
        Index(
            value = ["music_data"],
            unique = true
        )
    ]
)
data class Music(
    @ColumnInfo(name = "music_name") val name: String,
    @ColumnInfo(name = "music_time") val time: Int,
    @ColumnInfo(name = "music_data") val data: String,
    @ColumnInfo(name = "music_album_id") val albumId: Long,
    @ColumnInfo(name = "music_artist_id") val artistId: Long,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "music_id") val id: Long = 0,
) {
    @ColumnInfo(name = "music_is_liked")
    var isLiked: Boolean = false

    companion object {
        const val TABLE_NAME = "music_table"
        val empty by lazy {
            Music(
                name = "Select a Music.",
                time = 0,
                data = "",
                albumId = -1,
                artistId = -1,
                id = -1
            )
        }
    }

    private infix fun Long.remain(other: Int): Int {
        return (this % other).toInt()
    }

    // TODO: make share first image set first
    suspend fun getAlbumImage(): Any {
        val index = id remain MUSIC_PER_PAGE
        if (index < 0) return R.drawable.music_player_icon
        val (id, bitmap) = musicBitmaps[index]
        return if (id == this.id) {
            bitmap
        } else {
            pathToBitmap(data).apply {
                musicBitmaps[index] = Pair(this@Music.id, this)
            }
        } ?: R.drawable.music_player_icon
    }
}

