package com.example.musicplayer.notification

import com.example.musicplayer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.scopes.ServiceScoped
import java.util.*

@ServiceScoped
class NotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val manager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val notificationId: Int = 123456
    private val channelId: String = "channel_id"
    private val channelName: String = "channel_name"
    @RequiresApi(Build.VERSION_CODES.N)
    private val importance: Int = NotificationManager.IMPORTANCE_LOW

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
        contextIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_music)
            .setCustomContentView(smallView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contextIntent)
            .setAutoCancel(true)
    }

    fun getNotificationBuilder(
        smallView: RemoteViews,
        contextIntent: PendingIntent
    ): NotificationCompat.Builder {
        return createNotification(
            smallView, contextIntent
        )
    }

    fun startNotification(builder: NotificationCompat.Builder) {
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun cancelNotification() {
        manager.cancel(notificationId)
    }
}
