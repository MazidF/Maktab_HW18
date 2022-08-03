package com.example.musicplayer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.musicplayer.data.local.data_store.music.MusicDataStore
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.domain.controller.MusicHandler
import com.example.musicplayer.domain.controller.MusicManager
import com.example.musicplayer.notification.NotificationHandler
import com.example.musicplayer.service.receiver.SCI
import com.example.musicplayer.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SuperMusicService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val binder by lazy {
        MusicBinder()
    }

    // TODO: set these in binder
    private var musicIndex: Int? = null
    private var musicLists: MusicLists? = null

    //////////////////////////injects//////////////////////////

    @Inject
    lateinit var useCase: MusicUseCase

    @Inject
    lateinit var musicManager: MusicManager

    @Inject
    lateinit var musicDataStore: MusicDataStore

    @Inject
    lateinit var notificationHandler: NotificationHandler

    //////////////////////////injects//////////////////////////


    override fun onCreate() {
        super.onCreate()
        init()
        observe()
    }


    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////

    private fun init() {
        with(musicManager) {
            setOnPrepared {
                updateMusicDuration(musicManager.duration())
            }
        }
        loadState()
    }

    private fun observe() {
        val last = MusicHandler().apply {
            changeMusic(Music.empty)
        }
        with(musicManager) {
            musicHandler.liveData().observeForever {
                if (it.hasBeenSetup()) {
                    if (last.music != it.music) {
                        updateMusic(it.music)
                    }
                    if (last.isPlaying != it.isPlaying) {
                        updateMusicState(it.isPlaying)
                    }
                    last.apply {
                        this.isPlaying = it.isPlaying
                        this.music = it.music
                    }
                }
            }
        }
    }

    private fun getArtist(artistId: Long): Artist? {
        return useCase.artistMapStateFlow.value[artistId]
    }

    private fun loadState() = scope.launch {
        val info = musicDataStore.preferences.first()
        with(info) {
            val musicIndex = if (info.musicIndex == -1) 0 else info.musicIndex
            useCase.getMusicListFrom(musicList).first {
                it.isNotEmpty()
            }.also {
                changeMusicList(it, musicIndex)
            }

            changeShuffle(info.hasShuffle)

            // TODO: seek after setting data and prepare
//            musicManager.seekTo(info.musicPosition)
        }
    }

    private fun saveState() = scope.launch {
        musicLists?.let {
            musicDataStore.updateMusicList(it)
        }
        musicIndex?.let {
            musicDataStore.updateMusicIndex(it)
        }
        musicManager.getMusicCurrentTime().let {
            musicDataStore.updateMusicPosition(it)
        }
        musicManager.let {
            musicDataStore.updateHasShuffle(it.hasShuffle())
        }
    }

    /////////////////manager_controller/////////////////

    private fun next() {
        musicManager.next()
    }

    private fun prev() {
        musicManager.prev()
    }

    private fun playOrPause() {
        musicManager.playOrPause()
    }

    private fun changeMusicList(musicLists: MusicLists, index: Int) = scope.launch {
        if (this@SuperMusicService.musicLists != musicLists) {
            this@SuperMusicService.musicLists = musicLists
            val list = useCase.getMusicListFrom(musicLists).first { it.isNotEmpty() }
            musicManager.setupMusicList(list, index, true)
        } else {
            changeMusicIndex(index)
        }
    }

    private fun changeMusicList(newList: List<Music>, index: Int, fromUser: Boolean = false) {
        musicManager.setupMusicList(newList, index, fromUser)
    }

    private fun changeMusicIndex(newIndex: Int) {
        musicManager.setupMusicPosition(newIndex, true)
    }

    private fun changeShuffle(hasShuffle: Boolean) {
        musicManager.setupMusicShuffle(hasShuffle)
    }

    private fun isPlaying() = musicManager.isPlaying()

    /////////////////manager_controller/////////////////


    /////////////////output_controller/////////////////

    private fun updateMusic(music: Music) = scope.launch {
        useCase.updateMusic(music)
        notificationHandler.updateNotification(
            music = music,
            artist = getArtist(music.artistId)?.name ?: Constants.UNKNOWN
        )
    }

    private fun updateMusicDuration(duration: Int) = scope.launch {
        useCase.updateMusicDuration(duration)
    }

    private fun updateMusicState(isPlaying: Boolean) = scope.launch {
        useCase.updateMusicIsPlaying(isPlaying)
        notificationHandler.updateNotification(isPlaying)
    }

    private fun updateMusicShuffle(hasShuffle: Boolean) = scope.launch {
        useCase.updateMusicShuffle(hasShuffle)
    }

    /////////////////output_controller/////////////////


    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY
        when (action) {
            SCI.NEXT.name -> next()
            SCI.PLAY_PAUSE.name -> playOrPause()
            SCI.PREV.name -> prev()
            SCI.START_NOTIFICATION.name -> startNotification()
            SCI.END_NOTIFICATION.name -> endNotification()
            SCI.END_SERVICE.name -> scope.launch {
                saveState().join()
                stopSelf(startId)
            }
        }
        return START_NOT_STICKY
    }

    private fun startNotification() {
        val notification = notificationHandler.createNotification()
        notificationHandler.startNotification(notification)
        val music = musicManager.musicHandler.valueNotNull().music
        notificationHandler.updateNotification(music, getArtist(music.artistId)?.name ?: Constants.UNKNOWN)
        notificationHandler.updateNotification(isPlaying())
        startForeground(notificationHandler.notificationId, notification)
//         stopForeground
    }

    private fun endNotification() {
        stopForeground(false)
        notificationHandler.cancelNotification()
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        saveState()
        super.onDestroy()
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, SuperMusicService::class.java)
    }


    inner class MusicBinder : Binder() {
        private val service = this@SuperMusicService

        fun syncSeekbar(): Flow<Int> {
            return musicManager.syncSeekbar()
        }

        fun changeList(musicLists: MusicLists, musicIndex: Int) {
            changeMusicList(musicLists, musicIndex)
        }

        fun changeIndex(newIndex: Int) {
            changeMusicIndex(newIndex)
        }

        fun shuffle() {
            musicManager.shuffle()
            updateMusicShuffle(musicManager.hasShuffle())
        }

        fun isPlaying(): Boolean {
            return service.isPlaying()
        }

        fun seekTo(pos: Int) {
            musicManager.seekTo(pos)
        }

        fun next() {
            service.next()
        }

        fun prev() {
            service.prev()
        }

        fun playOrPause() {
            service.playOrPause()
        }

        fun getShuffle(): Boolean {
            return musicManager.hasShuffle()
        }
    }
}