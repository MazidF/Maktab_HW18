package com.example.musicplayer.ui.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Music
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
