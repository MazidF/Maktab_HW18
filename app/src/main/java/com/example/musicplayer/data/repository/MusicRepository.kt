package com.example.musicplayer.data.repository

import android.content.Context
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.di.annotations.DispatcherIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext


// TODO: add dispatcher
class MusicRepository(
    private val local: MusicLocalRepository,
    private val remote: MusicRemoteRepository,
    private val dispatcher: CoroutineContext = Dispatchers.IO,
) {

    fun getAllMusics(context: Context?): Flow<List<Music>> {
        return local.getAllMusics(context).flowOn(dispatcher)
    }

}