package com.example.musicplayer.data.local.data_store.music

class MusicPreferenceInfo(
    val musicList: MusicLists,
    val musicIndex: Int,
    val hasShuffle: Boolean,
    val musicState: MusicState,
) {
    // TODO: change minus method
    operator fun minus(other: MusicPreferenceInfo): Triple<MusicLists?, Int?, Boolean?> {
        val list = if (this.musicList == other.musicList) null else this.musicList
        val index = if (this.musicIndex == other.musicIndex) null else this.musicIndex
        val hasShuffle = if (this.hasShuffle == other.hasShuffle) null else this.hasShuffle
        return Triple(
            list, index, hasShuffle
        )
    }

    fun asTriple() = Triple(
        musicList, musicIndex, hasShuffle
    )
}
