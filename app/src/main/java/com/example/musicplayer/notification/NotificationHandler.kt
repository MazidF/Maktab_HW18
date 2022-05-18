package com.example.musicplayer.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.service.SuperMusicService
import com.example.musicplayer.service.receiver.MusicReceiver
import com.example.musicplayer.service.receiver.SCI
import com.example.musicplayer.ui.activity.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.*
import javax.inject.Inject

@ServiceScoped
class NotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var updateJob: Job? = null

    private val manager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    val notificationId: Int = 123456
    private val channelId: String = "channel_id"
    private val channelName: String = "channel_name"
    @RequiresApi(Build.VERSION_CODES.N)
    private val importance: Int = NotificationManager.IMPORTANCE_LOW

    private var hasStared = false
    private var notification: Notification? = null
    private var notificationViews: Pair<RemoteViews, RemoteViews>? = null

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                importance
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(
        smallView: RemoteViews,
        bigView: RemoteViews,
        contextIntent: PendingIntent,
        deleteIntent: PendingIntent,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_music)
            .setCustomContentView(smallView)
            .setCustomBigContentView(bigView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contextIntent)
            .setDeleteIntent(deleteIntent)
            .setAutoCancel(true)
            .setOngoing(false)
    }

    fun createNotification(): Notification {
        val (small, big) = createRemoteViews().also {
            notificationViews = it
        }
        return createNotification(
            smallView = small,
            bigView = big,
            contextIntent = PendingIntent.getActivity(
                context,
                1234,
                MainActivity.getIntent(context),
                PendingIntent.FLAG_IMMUTABLE
            ),
            deleteIntent = createPending(SCI.END_NOTIFICATION)
        ).build()
    }

    @SuppressLint("RemoteViewLayout")
    private fun createRemoteViews(): Pair<RemoteViews, RemoteViews> {
        val small = RemoteViews(context.packageName, R.layout.notification_small)
        val big = RemoteViews(context.packageName, R.layout.notification_big)

        small.apply {
            setOnClickPendingIntent(R.id.notification_next, createPending(SCI.NEXT))
            setOnClickPendingIntent(R.id.notification_pause_play, createPending(SCI.PLAY_PAUSE))
            setOnClickPendingIntent(R.id.notification_prev, createPending(SCI.PREV))
        }
        big.apply {
            setOnClickPendingIntent(R.id.notification_next, createPending(SCI.NEXT))
            setOnClickPendingIntent(R.id.notification_pause_play, createPending(SCI.PLAY_PAUSE))
            setOnClickPendingIntent(R.id.notification_prev, createPending(SCI.PREV))
        }

        return Pair(small, big)
    }

    private fun createPending(sci: SCI): PendingIntent {
        val intent = Intent(context, MusicReceiver::class.java).apply {
            action = sci.name
        }
        return PendingIntent.getBroadcast(context, 123, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun startNotification(notification: Notification) {
        if (hasStared.not()) {
            this.notification = notification
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            hasStared = true
        }
    }

    fun cancelNotification() {
        if (hasStared) {
            manager.cancel(notificationId)
            notificationViews = null
            notification = null
            hasStared = false
        }
    }

    fun updateNotification(music: Music, artist: String) {
        if (hasStared) {
            val (small, big) = notificationViews ?: return
            scope.launch {
                val image = music.getAlbumImage()
                small.apply {
                    if (image is Bitmap) {
                        setImageViewBitmap(R.id.notification_image, image)
                    } else {
                        setImageViewResource(R.id.notification_image, image as Int)
                    }
                }
                big.apply {
                    if (image is Bitmap) {
                        setImageViewBitmap(R.id.notification_image, image)
                    } else {
                        setImageViewResource(R.id.notification_image, image as Int)
                    }
                }
            }
            small.apply {
                setTextViewText(R.id.notification_name, music.name)
                setTextViewText(R.id.notification_artist, artist)
            }
            big.apply {
                setTextViewText(R.id.notification_name, music.name)
                setTextViewText(R.id.notification_artist, artist)
            }

            notifyUpdate()
        }
    }

    fun updateNotification(isPlaying: Boolean) {
        if (hasStared) {
            val (small, big) = notificationViews ?: return
            val id = if (isPlaying.not()) R.drawable.ic_play else R.drawable.ic_pause
            small.apply {
                setImageViewResource(R.id.notification_pause_play, id)
            }
            big.apply {
                setImageViewResource(R.id.notification_pause_play, id)
            }

            notifyUpdate()
        }
    }

    private fun notifyUpdate() = scope.launch {
        if (hasStared) {
            updateJob?.cancelAndJoin()
            updateJob = scope.launch {
                notification?.let {
                    NotificationManagerCompat.from(context).notify(notificationId, it)
                }
            }
        }
    }
}
