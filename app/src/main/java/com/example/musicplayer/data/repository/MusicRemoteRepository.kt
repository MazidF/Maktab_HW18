package com.example.musicplayer.data.repository

import com.example.musicplayer.di.annotations.DispatcherIO
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


// TODO: add dispatcher
class MusicRemoteRepository @Inject constructor(
    @DispatcherIO private val dispatcher: CoroutineContext,
)
