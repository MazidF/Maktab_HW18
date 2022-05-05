package com.example.musicplayer.ui.model

import com.example.musicplayer.data.model.Music

data class SelectableMusic(
    val music: Music,
    var isSelected : Boolean? = false
) {

    constructor(selectableMusic: SelectableMusic) : this(selectableMusic.music, selectableMusic.isSelected)

    fun selectAndUnSelect() {
        isSelected = isSelected!!.not()
    }

    companion object {
        var selectionStateChenged = false
    }
}
