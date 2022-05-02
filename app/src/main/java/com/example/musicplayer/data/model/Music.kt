package com.example.musicplayer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "music_time") val time: String,
    @ColumnInfo(name = "music_data") val data: String,
    @ColumnInfo(name = "music_album_id") val albumId: Long,
    @ColumnInfo(name = "music_artist_id") val artistId: Long,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "music_id") val id: Long = 0,
) {
    @ColumnInfo(name = "music_is_liked") var isLiked: Boolean = false

    companion object {
        const val TABLE_NAME = "music_table"
        val empty by lazy {
            Music(
                name = "",
                time = "",
                data = "",
                albumId = -1,
                artistId = -1,
                id = -1
            )
        }
    }
}

