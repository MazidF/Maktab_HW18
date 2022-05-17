package com.example.musicplayer.utils

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object Constants {
    const val UNKNOWN = "Unknown"
    const val UNKNOWN_ID = -1L

    const val HAS_BEEN_LOADED = "hasBeenLoaded"

    const val ALBUM_ART_PATH = "content://media/external/audio/albumart"

    val glideDiskCacheStrategy = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)

    const val MUSIC_PER_PAGE: Int = 40 // even more :)

    const val ALBUM_OR_ARTIST_PER_PAGE = 10 // even less :)

    val musicBitmaps = Array<Pair<Long, Bitmap?>>(MUSIC_PER_PAGE) {
        0L to null
    }
}