package com.example.musicplayer.data.model

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class AlbumInfo(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "album_id",
        entityColumn = "music_album_id",
        entity = Music::class
    ) val musics: List<Music>
): Serializable {
    fun musicsLength() = musics.sumOf { it.time }
}
