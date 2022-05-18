package com.example.musicplayer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.musicplayer.utils.Constants.MUSIC_PLAYER_ICON_ID
import com.example.musicplayer.utils.Constants.musicBitmaps
import com.example.musicplayer.utils.getOrSaveDefault
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

    fun getAlbumImage(): Any {
        val index = albumId
        if (index < 0) return MUSIC_PLAYER_ICON_ID
        return musicBitmaps.getOrSaveDefault(index) {
            pathToBitmap(data) ?: MUSIC_PLAYER_ICON_ID
        }
    }
}

