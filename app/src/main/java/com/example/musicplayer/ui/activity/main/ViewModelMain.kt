package com.example.musicplayer.ui.activity.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.local.data_store.music.MusicDataStore
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.data.local.data_store.music.MusicState
import com.example.musicplayer.data.local.data_store.music.MusicState.Companion.empty
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.domain.controller.MusicManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelMain @Inject constructor(
    private val useCase: MusicUseCase,
    private val musicDataStore: MusicDataStore,
    private val manager: MusicManager
) : ViewModel() {
    private val _musicStateStateFlow = MutableStateFlow<MusicState>(empty)
    val musicStateStateFlow get() = _musicStateStateFlow.asStateFlow()

    private val _musicsStateFlow = MutableStateFlow<List<Music>>(listOf())
    val musicsStateFlow get() = _musicsStateFlow.asStateFlow()

    private val _currentMusicStateFlow = MutableStateFlow<Music>(Music.empty)
    val currentMusicStateFlow get() = _currentMusicStateFlow.asStateFlow()

    private val _hasShuffleStateFlow = MutableStateFlow<Boolean>(false)
    val hasShuffleStateFlow get() = _hasShuffleStateFlow.asStateFlow()

    private val _musicIndexStateFlow = MutableStateFlow<Int>(-1)
    val musicIndexStateFlow get() = _musicIndexStateFlow.asStateFlow()

    private val _musicDurationStateFlow = MutableStateFlow(0)
    val musicDurationStateFlow get() = _musicDurationStateFlow.asStateFlow()

    var isPlaying = MutableLiveData<Boolean>()
    private var currentMusicState: MusicState? = null
    private var currentMusicLists: MusicLists? = null
    private var currentMusicIndex: Int? = null
    private var currentHasShuffle: Boolean? = null

    init {
        // TODO: change this part of code
        instance = this
    }

    fun setup() {
        viewModelScope.launch {
            musicDataStore.preferences.collect {
                setMusicList(it.musicList, it.musicIndex).join()
                setShuffle(it.hasShuffle).join()
                setMusicState(it.musicState)
            }
        }
        viewModelScope.launch {
            musicsStateFlow.collect {
                manager.setupMusicList(it, getCurrentMusicPosition())
            }
        }
        viewModelScope.launch {
            hasShuffleStateFlow.collect {
                manager.setupMusicShuffle(it)
            }
        }
        viewModelScope.launch {
            musicIndexStateFlow.collect {
                if (it >= 0) {
                    manager.setupMusicPosition(it)
                }
            }
        }
        viewModelScope.launch {
            musicStateStateFlow.collect {
                manager.setupMusicState(it)
            }
        }
        manager.setOnPrepared {
            viewModelScope.launch {
                val lastState = _musicStateStateFlow.value
                if (isPlaying.value != null && lastState.musicIsPlaying.not()) {
                    val state = MusicState(
                        lastState.musicPosition,
                        true
                    )
                    updateMusicState(state)
                } else {
                    isPlaying.value = false
                }
                _musicDurationStateFlow.emit(manager.duration())
            }
        }
        manager.setOnCompletion {
            next()
        }
    }

    private fun setMusicState(musicState: MusicState) {
        if (currentMusicState != musicState) {
            currentMusicState = musicState
            viewModelScope.launch {
                _musicStateStateFlow.emit(musicState)
            }
        }
    }

    private fun setMusicList(musicLists: MusicLists, musicIndex: Int): Job {
        return viewModelScope.launch {
            if (currentMusicLists != musicLists) {
                currentMusicLists = musicLists
                val list = useCase.getMusicListFrom(musicLists).first {
                    it.isNotEmpty()
                }
                _musicsStateFlow.emit(list)
            }
            currentMusicIndex = musicIndex
            _musicIndexStateFlow.emit(musicIndex)
            _currentMusicStateFlow.emit(_musicsStateFlow.value[musicIndex])
        }
    }

    private fun setShuffle(hasShuffle: Boolean): Job {
        return viewModelScope.launch {
            _hasShuffleStateFlow.emit(hasShuffle)
        }
    }

    fun updateMusicList(musicLists: MusicLists, id: Long?, musicIndex: Int) {
        if (currentMusicLists != musicLists) {
            viewModelScope.launch {
                musicDataStore.updateMusicList(musicLists, id)
            }
        }
        viewModelScope.launch {
            musicDataStore.updateMusicIndex(musicIndex)
        }
    }

    fun updateShuffle(hasShuffle: Boolean) {
        if (currentHasShuffle != hasShuffle) {
            currentHasShuffle = hasShuffle
            viewModelScope.launch {
                musicDataStore.updateHasShuffle(hasShuffle)
            }
        }
    }

    fun updateMusicState(musicState: MusicState) {
        if (currentMusicState != musicState) {
            viewModelScope.launch {
                musicDataStore.updateMusicState(musicState)
            }
        }
    }

    fun getShuffle() = currentHasShuffle == true

    fun getCurrentMusicPosition(): Int {
        return currentMusicIndex ?: -1
    }

    fun getArtist(artistId: Long): Artist? {
        return useCase.artistMapStateFlow.value[artistId]
    }

    fun next() {
        viewModelScope.launch {
            musicDataStore.updateMusicIndex(currentMusicIndex?.plus(1) ?: 0)
        }
    }

    fun prev() {
        viewModelScope.launch {
            musicDataStore.updateMusicIndex(currentMusicIndex?.minus(1) ?: 0)
        }
    }

    fun shuffle() {
        viewModelScope.launch {
            musicDataStore.updateHasShuffle((currentHasShuffle == true).not())
        }
    }

    fun playOrPause() {
        isPlaying.value = isPlaying.value!!.not()
        viewModelScope.launch {
            val lastState = _musicStateStateFlow.value
            updateMusicState(
                MusicState(
                    musicIsPlaying = lastState.musicIsPlaying.not(),
                    musicPosition = lastState.musicPosition,
                )
            )
        }
    }

    fun syncSeekbar(): Flow<Int> {
        return manager.syncSeekbar()
    }

    fun seekTo(pos: Int) {
        manager.seekTo(pos)
    }

    fun musicDuration(): Int {
        return manager.duration()
    }

    fun saveState() {
        viewModelScope.launch {
            updateMusicState(
                MusicState(
                    musicPosition = currentMusicIndex ?: -1,
                    musicIsPlaying = false
                )
            )
        }
    }

    companion object {
        private var instance: ViewModelMain? = null
        fun getInstance() = instance!! // not a good code :)
    }
}
