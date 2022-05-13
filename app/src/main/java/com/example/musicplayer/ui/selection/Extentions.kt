package com.example.musicplayer.ui.selection

import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView

fun createSelectionTracker(id: String, recyclerView: RecyclerView): SelectionTracker<Long> {
    return SelectionTracker.Builder<Long>(
        id,
        recyclerView,
        MusicItemKeyProvider(recyclerView),
        MusicItemDetailLookup(recyclerView),
        StorageStrategy.createLongStorage()
    ).withSelectionPredicate(
        SelectionPredicates.createSelectAnything()
    ).build()
}