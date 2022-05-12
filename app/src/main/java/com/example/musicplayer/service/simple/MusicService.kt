package com.example.musicplayer.service.simple

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.musicplayer.data.local.data_store.music.MusicDataStore
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.data.local.data_store.music.MusicPreferenceInfo
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.domain.controller.MusicManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {
    // add SupervisorJob() makes it cancelable
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val binder by lazy {
        MusicBinder()
    }
    @Inject lateinit var manager: MusicManager
    @Inject lateinit var useCase: MusicUseCase
    @Inject lateinit var musicDatastore: MusicDataStore

    private var bindCounter = 0
    fun isBounded() = bindCounter != 0

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        var lastInfo: MusicPreferenceInfo? = null
        scope.launch {
            musicDatastore.preferences.collect { info ->
                val diff = if (lastInfo == null) {
                    info.asTriple()
                } else {
                    info - lastInfo!!
                }
                manager.setupMusicList(
                    list = diff.first?.let { getListAsync(it).await() },
                    hasShuffle = diff.third,
                    startPosition = diff.second
                )
                lastInfo = info
            }
        }
    }

    private fun getListAsync(musicLists: MusicLists): Deferred<List<Music>> {
        return scope.async {
            useCase.getMusicListFrom(musicLists).firstOrNull() ?: listOf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            commandHandler(it)
        }
        return START_STICKY
    }

    private fun commandHandler(intent: Intent) {

    }

    inner class MusicBinder : Binder() {
        fun doTask(block: (MusicService) -> Unit) {
            block(this@MusicService)
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        bindCounter++
        return binder
    }

    override fun onRebind(intent: Intent?) {
        bindCounter++
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        bindCounter--
        return super.onUnbind(intent)
    }
}

enum class ServiceControlInput {
    NEXT,
    PREV,
    PLAY_PAUSE,
    OPEN_APP,
    CLOSE
}