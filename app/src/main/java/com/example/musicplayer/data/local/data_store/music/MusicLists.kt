package com.example.musicplayer.data.local.data_store.music

sealed class MusicLists {
    object TRACKS: MusicLists()

    object FAVORITES: MusicLists()

    class ALBUMS(
        val albumId: Long,
    ): MusicLists()

    class ARTISTS(
        val artistId: Long,
    ): MusicLists()

    fun name(): String = this::class.java.simpleName
}