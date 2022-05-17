package com.example.musicplayer.domain.controller

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.SeekBar
import com.example.musicplayer.data.local.data_store.music.MusicState
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.utils.LiveDataWrapper
import com.example.musicplayer.utils.shuffleIntArray
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

class MusicManager : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener {

    private var musics: List<Music> = listOf()
    private var shuffle: List<Int>? = null
    private val player: MediaPlayer = MediaPlayer().apply {
        setOnCompletionListener(this@MusicManager)
        setOnPreparedListener(this@MusicManager)
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }
    val musicHandler = LiveDataWrapper<MusicHandler>()
    private var currentPosition = LiveDataWrapper(-1)

    private var onCompletion: () -> Unit = {next()}
    fun setOnCompletion(onCompletion: () -> Unit) {
        this.onCompletion = onCompletion
    }

    private var onPrepared: () -> Unit = {start()}
    fun setOnPrepared(onPrepared: () -> Unit) {
        this.onPrepared = {
            onPrepared()
            if (musicHandler.value() == null) {
                musicHandler.setValue(MusicHandler())
            } else {
                start()
            }
        }
    }

    override fun onCompletion(player: MediaPlayer) {
        onCompletion()
    }

    override fun onPrepared(player: MediaPlayer) {
        onPrepared()
    }

    override fun onError(player: MediaPlayer, what: Int, extra: Int): Boolean {
        throw Exception("OnError!!")
    }

    private fun changeShuffleState(hasShuffle: Boolean) {
        shuffle = if (hasShuffle) {
            shuffleIntArray(musics.size)
        } else {
            null
        }
    }

    fun setupMusicPosition(position: Int) {
        val temp = shuffle
        shuffle = null
        setupMusic(position)
        shuffle = temp
    }

    fun setupMusicList(list: List<Music>, startPosition: Int = 0) {
        // TODO: setup position make problem
        if (list.isEmpty()) return
        musics = list
        if (startPosition >= 0) {
            setupMusicPosition(startPosition)
        }
    }

    fun setupMusicShuffle(hasShuffle: Boolean) {
        changeShuffleState(hasShuffle)
    }

    private fun getRealPosition(position: Int): Int {
        return shuffle?.get(position) ?: position
    }

    private fun getMusic(position: Int): Music {
        return musics[position]
    }

    private fun setupMusic(position: Int) {
        currentPosition.postValue(position)
        setupMusic(getMusic(getRealPosition(position)))
    }

    private fun setupMusic(music: Music) {
        reset()
        setDataSource(music)
        prepare()
    }

    private fun setDataSource(music: Music) {
        player.setDataSource(music.data)
        musicHandler.apply {
            it.changeMusic(music)
        }
    }

    private fun start() {
        player.start()
        musicHandler.apply {
            it.isPlaying = true
        }
    }

    private fun prepare() {
        player.prepareAsync()
    }

    private fun pause() {
        player.pause()
        musicHandler.apply {
            it.isPlaying = false
        }
    }

    private fun stop() {
        player.stop()
    }

    private fun reset() {
        stop()
        player.reset()
    }

    fun getMusicCurrentTime(): Int {
        return if (player.isPlaying) {
            player.currentPosition
        } else {
            -1
        }
    }

    fun next() {
        val pos = (currentPosition.valueNotNull() + 1) % musics.size
        setupMusic(pos)
    }

    fun prev() {
        val pos = (currentPosition.valueNotNull() - 1 + musics.size) % musics.size
        setupMusic(pos)
    }

    fun leftDuration(): Int {
        return player.duration - player.currentPosition
    }

    fun syncSeekbar(): Flow<Int> {
        return flow {
            while (true) {
                emit(player.currentPosition)
                delay(1000)
            }
        }
    }

    fun setupMusicState(musicState: MusicState) {
        if (musicState.musicIsPlaying && player.isPlaying.not()) {
            start()
        } else if(musicState.musicIsPlaying.not() && player.isPlaying) {
            pause()
        }
        if (musicState.musicPosition >= 0) {
            player.seekTo(musicState.musicPosition)
        }
    }

    fun seekTo(pos: Int) {
        player.seekTo(pos)
    }

    fun duration(): Int {
        return player.duration
    }
}
