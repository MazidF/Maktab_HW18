package com.example.musicplayer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Artist.TABLE_NAME)
data class Artist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "artist_id") val id: Long,
    @ColumnInfo(name = "artist_name") val name: String,
) {
    companion object {
        const val TABLE_NAME = "artist_table"
    }
}