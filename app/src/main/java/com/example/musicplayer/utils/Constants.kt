package com.example.musicplayer.utils

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object Constants {
    const val UNKNOWN = "Unknown"
    const val UNKNOWN_ID = -1L

    const val HAS_BEEN_LOADED = "hasBeenLoaded"

    const val ALBUM_ART_PATH = "content://media/external/audio/albumart"

    val glideDiskCacheStrategy = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
}