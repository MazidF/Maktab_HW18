package com.example.musicplayer.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.musicplayer.ui.activity.main.ViewModelMain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import com.example.musicplayer.service.ServiceControlInput.*

@AndroidEntryPoint
class MusicService : Service() {
    // add SupervisorJob() makes it cancelable
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val binder by lazy {
        MusicBinder()
    }

    @Inject
    lateinit var viewModel: ViewModelMain

    private var bindCounter = 0
    fun isBounded() = bindCounter != 0

    override fun onCreate() {
        super.onCreate()
        observe()
    }

    // TODO: move observe() to viewModel
    private fun observe() = with(viewModel) {
        scope.launch {
            musicsStateFlow.collect {
                manager.setupMusicList(it, getCurrentMusicPosition())
            }
        }
        scope.launch {
            hasShuffleStateFlow.collect {
                manager.setupMusicShuffle(it)
            }
        }
        scope.launch {
            musicIndexStateFlow.collect {
                manager.setupMusicPosition(it)
            }
        }
        scope.launch {
            musicStateStateFlow.collect {
                manager.setupMusicState(it)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            commandHandler(it)
        }
        return START_STICKY
    }

    private fun commandHandler(intent: Intent) {
        when(intent.action) {
            NEXT.name -> {
                viewModel.next()
            }
            PREV.name -> {
                viewModel.prev()
            }
            PLAY_PAUSE.name -> {
                viewModel.playOrPause()
            }
            OPEN_APP.name -> {
                // TODO: use pending intent in notification
            }
            CLOSE.name -> {

            }
            else -> {

            }
        }
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
