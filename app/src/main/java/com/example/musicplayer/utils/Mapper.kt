package com.example.musicplayer.utils

import com.example.musicplayer.data.model.Music
import com.example.musicplayer.ui.model.SelectableMusic

object Mapper {

    fun Music.toSelectableMusic(default: Boolean? = false) = SelectableMusic(this, default)

}