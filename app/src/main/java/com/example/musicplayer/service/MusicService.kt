package com.example.musicplayer.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import com.example.musicplayer.R
import com.example.musicplayer.data.local.data_store.music.MusicDataStore
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.notification.NotificationHandler
import com.example.musicplayer.service.ServiceControlInput.*
import com.example.musicplayer.ui.activity.main.MainActivity
import com.example.musicplayer.ui.activity.main.ViewModelMain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {
    // add SupervisorJob() makes it cancelable
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val binder by lazy {
        MusicBinder(this)
    }

    @Inject
    lateinit var notificationManager: NotificationHandler

    @Inject
    lateinit var useCase: MusicUseCase

    @Inject
    lateinit var musicDataStore: MusicDataStore

    val viewModel by lazy {
        ViewModelMain.getInstance()
    }

    private var bindCounter = 0
    fun isBounded() = bindCounter != 0

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() = with(viewModel) {
        setup()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            commandHandler(it)
        }
        return START_STICKY
    }

    private fun commandHandler(intent: Intent) {
        when (intent.action) {
            NEXT.name -> {
                viewModel.next()
            }
            PREV.name -> {
                viewModel.prev()
            }
            PLAY_PAUSE.name -> {
                viewModel.playOrPause()
            }
            CLOSE.name -> {
                notificationManager.cancelNotification()
            }
            START.name -> {
                startNotification()
            }
        }
    }

    private fun startNotification() {
        val view = createView()
        val builder = notificationManager.getNotificationBuilder(
            view,
            PendingIntent.getActivity(
                this,
                1,
                MainActivity.getIntent(this),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        notificationManager.startNotification(builder)
    }

    @SuppressLint("RemoteViewLayout")
    private fun createView(): RemoteViews {
        return RemoteViews(packageName, R.layout.notification_small).apply {
            setOnClickPendingIntent(R.id.notification_next, createPendingIntent(NEXT))
            setOnClickPendingIntent(R.id.notification_pause_play, createPendingIntent(PLAY_PAUSE))
            setOnClickPendingIntent(R.id.notification_prev, createPendingIntent(PREV))
        }
    }

    // TODO: search about flag
    private fun createPendingIntent(input: ServiceControlInput): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        return PendingIntent.getService(this, input.ordinal, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    class MusicBinder(
        private val service: MusicService
    ) : Binder() {
        fun doTask(block: (MusicService) -> Unit) {
            block(service)
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

    override fun onDestroy() {
        viewModel.saveState()
        super.onDestroy()
    }
}
