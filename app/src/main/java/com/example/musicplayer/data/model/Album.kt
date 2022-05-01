package com.example.musicplayer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Album.TABLE_NAME)
data class Album(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "album_id") val id: Long,
    @ColumnInfo(name = "album_name") val name: String,
) {
    companion object {
        const val TABLE_NAME = "album_table"
    }
}