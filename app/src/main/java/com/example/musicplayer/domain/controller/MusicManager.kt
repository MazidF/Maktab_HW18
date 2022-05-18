package com.example.musicplayer.domain.controller

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.utils.LiveDataWrapper
import com.example.musicplayer.utils.shuffleIntArray
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MusicManager : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var setupJob: Job? = null

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
    val musicHandler = LiveDataWrapper<MusicHandler>(MusicHandler())
    private var currentPosition = LiveDataWrapper(-1)

    private var onCompletion: () -> Unit = { next() }
    fun setOnCompletion(onCompletion: () -> Unit) {
        this.onCompletion = onCompletion
    }

    private var onPrepared: () -> Unit = { start() }
    fun setOnPrepared(onPrepared: () -> Unit) {
        this.onPrepared = {
            onPrepared()
            if (musicHandler.valueNotNull().isPlaying) {
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

    fun setupMusicPosition(position: Int, fromUser: Boolean) {
        val temp = shuffle
        shuffle = null
        setupMusic(position, fromUser)
        shuffle = temp
    }

    fun setupMusicList(list: List<Music>, startPosition: Int, fromUser: Boolean) {
        if (list.isEmpty()) return
        musics = list
        if (/*fromUser && */startPosition >= 0) {
            setupMusicPosition(startPosition, fromUser)
        }
    }

    fun setupMusicShuffle(hasShuffle: Boolean) {
        changeShuffleState(hasShuffle)
    }

    fun shuffle() {
        changeShuffleState((shuffle != null).not())
    }

    private fun getRealPosition(position: Int): Int {
        return shuffle?.get(position) ?: position
    }

    private fun getMusic(position: Int): Music {
        return musics[position]
    }

    private fun setupMusic(position: Int, fromUser: Boolean) {
        currentPosition.postValue(position)
        setupMusic(getMusic(getRealPosition(position)), fromUser)
    }

    private fun setupMusic(music: Music, fromUser: Boolean) = scope.launch {
        setupJob?.cancelAndJoin()
        setupJob = scope.launch {
            reset()
            setDataSource(music)
            if (fromUser) {
                musicHandler.apply {
                    it.isPlaying = true
                }
            }
            prepare()
        }
    }

    private fun setDataSource(music: Music) {
        player.setDataSource(music.data)
        musicHandler.apply {
            it.changeMusic(music)
        }
    }

    private fun prepare() {
        player.prepareAsync()
    }

    private fun start() {
        player.start()
        musicHandler.apply {
            it.isPlaying = true
        }
    }

    private fun pause() {
        player.pause()
        musicHandler.apply {
            it.isPlaying = false
        }
    }

    private fun reset() {
        player.reset()
        if (musicHandler.valueNotNull().hasBeenSetup()) {
        }
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
        setupMusic(pos, false)
    }

    fun prev() {
        val pos = (currentPosition.valueNotNull() - 1 + musics.size) % musics.size
        setupMusic(pos, false)
    }

    fun durationThatLeft(): Int {
        return player.duration - player.currentPosition
    }

    // TODO: somehow delete player.currentPosition and use flow as pulse
    fun syncSeekbar(): Flow<Int> {
        return flow {
            while (true) {
                emit(player.currentPosition)
                delay(1000)
            }
        }
    }

    fun seekTo(pos: Int) {
        player.seekTo(pos)
    }

    fun duration(): Int {
        return player.duration
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun playOrPause() {
        if (isPlaying()) {
            pause()
        } else {
            start()
        }
    }

    fun hasShuffle(): Boolean {
        return shuffle != null
    }
}
