package com.example.musicplayer.data.local.data_store.music

sealed class MusicLists {
    class TRACKS: MusicLists() {
        override fun equals(other: Any?): Boolean {
            return other is TRACKS
        }
    }
    class FAVORITES: MusicLists() {
        override fun equals(other: Any?): Boolean {
            return other is FAVORITES
        }
    }
    data class ALBUMS(
        val albumId: Long,
    ): MusicLists()

    data class ARTISTS(
        val artistId: Long,
    ): MusicLists()

    fun name(): String = this::class.java.simpleName
}