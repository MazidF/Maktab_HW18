package com.example.musicplayer.domain.controller

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.SeekBar
import com.example.musicplayer.data.local.data_store.music.MusicState
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.utils.LiveDataWrapper
import com.example.musicplayer.utils.shuffleIntArray
import java.util.*

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
    private val musicHandler = LiveDataWrapper<MusicHandler>()
    private var currentPosition = LiveDataWrapper(-1)
    private var timer: Timer? = null

    override fun onPrepared(player: MediaPlayer) {
        start()
    }

    override fun onCompletion(player: MediaPlayer) {
        next()
    }

    override fun onError(player: MediaPlayer, what: Int, extra: Int): Boolean {
        return false
        // TODO Not yet implemented
    }

    private fun changeShuffleState(hasShuffle: Boolean) {
        shuffle = if (hasShuffle) {
            shuffleIntArray(musics.size)
        } else {
            null
        }
    }

    fun setupMusicPosition(position: Int) {
        setupMusic(position)
    }

    fun setupMusicList(list: List<Music>, startPosition: Int = 0) {
        musics = list
        setupMusicPosition(startPosition)
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
        currentPosition.setValue(position)
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
        musicHandler.apply {
            it.isPlaying = false
        }
    }

    private fun reset() {
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

    private fun timerTask(seekBar: SeekBar) = object : TimerTask() {
        override fun run() {
            seekBar.progress = player.currentPosition
        }
    }

    // TODO: do something else
    fun setupSeekbar(seekBar: SeekBar) {
        with(seekBar) {
            progress = player.currentPosition
            max = player.duration
        }
        timer = Timer().apply {
            scheduleAtFixedRate(
                timerTask(seekBar),
                1_000,
                leftDuration().toLong()
            )
        }
    }

    fun setupMusicState(musicState: MusicState) {
        if (musicState.musicIsPlaying) {
            start()
        } else {
            pause()
        }
        player.seekTo(musicState.musicPosition)
    }
}
