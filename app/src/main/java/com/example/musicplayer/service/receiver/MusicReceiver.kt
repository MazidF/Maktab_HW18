package com.example.musicplayer.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.musicplayer.service.receiver.ReceiverInput.*
import com.example.musicplayer.service.ServiceControlInput
import com.example.musicplayer.service.SuperMusicService

typealias SCI = ServiceControlInput

class MusicReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        when(action) {
            START_SERVICE.name -> {
                startService(context)
            }
            NEXT.name -> {
                context.sentToServiceWithAction(SCI.NEXT)
            }
            PREV.name -> {
                context.sentToServiceWithAction(SCI.PREV)
            }
            PLAY_PAUSE.name -> {
                context.sentToServiceWithAction(SCI.PLAY_PAUSE)
            }
/*            START_NOTIFICATION.name -> {
                context.sentToServiceWithAction(SCI.START_NOTIFICATION)
            }*/
            END_NOTIFICATION.name -> {
                context.sentToServiceWithAction(SCI.END_NOTIFICATION)
            }
            END_SERVICE.name -> {
                stopService(context)
            }
        }
    }

    private fun startService(context: Context) {
        val intent = SuperMusicService.getIntent(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun stopService(context: Context) {
        val intent = SuperMusicService.getIntent(context)
        context.stopService(intent)
    }

    private fun Context.sentToServiceWithAction(action: SCI) {
        val intent = SuperMusicService.getIntent(this).apply {
            this.action = action.name
        }
        startService(intent)
    }
}

