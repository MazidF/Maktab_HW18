package com.example.musicplayer.ui.activity.main

import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class ViewModelMain @Inject constructor(
    private val useCase: MusicUseCase,
    private val musicDataStore: MusicDataStore,
) : ViewModel() {
    private var _musicStateStateFlow = MutableStateFlow<MusicState>(empty)
    val musicStateStateFlow get() = _musicStateStateFlow.asStateFlow()

    private var _musicsStateFlow = MutableStateFlow<List<Music>>(listOf())
    val musicsStateFlow get() = _musicsStateFlow.asStateFlow()

    private var _currentMusicStateFlow = MutableStateFlow<Music>(Music.empty)
    val currentMusicStateFlow get() = _currentMusicStateFlow.asStateFlow()

    private var _hasShuffleStateFlow = MutableStateFlow<Boolean>(false)
    val hasShuffleStateFlow get() = _hasShuffleStateFlow.asStateFlow()

    private var _musicIndexStateFlow = MutableStateFlow<Int>(-1)
    val musicIndexStateFlow get() = _musicIndexStateFlow.asStateFlow()

    // TODO: change them to stateFlow.value
    private var currentMusicState: MusicState? = null
    private var currentMusicLists: MusicLists? = null
    private var currentMusicIndex: Int? = null
    private var currentHasShuffle: Boolean? = null

    init {
        viewModelScope.launch {
            musicDataStore.preferences.collect {
                setMusicList(it.musicList, it.musicIndex)
                setShuffle(it.hasShuffle)
                setMusicState(it.musicState)
            }
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

    private fun setMusicList(musicLists: MusicLists, musicIndex: Int) {
        if (currentMusicLists != musicLists) {
            currentMusicLists = musicLists
            viewModelScope.launch {
                _musicsStateFlow.emit(
                    useCase.getMusicListFrom(musicLists).first {
                        it.isNotEmpty()
                    }
                )
                _currentMusicStateFlow.emit(_musicsStateFlow.value[musicIndex])
                currentMusicIndex = musicIndex
            }
            return
        }
        if (currentMusicIndex != musicIndex) {
            currentMusicIndex = musicIndex
            viewModelScope.launch {
                _currentMusicStateFlow.emit(_musicsStateFlow.value[musicIndex])
            }
        }
    }

    private fun setShuffle(hasShuffle: Boolean) {
        viewModelScope.launch {
            _hasShuffleStateFlow.emit(hasShuffle)
        }
    }

    fun updateMusicList(musicLists: MusicLists, id: Long?, musicIndex: Int) {
        if (currentMusicLists != musicLists) {
            viewModelScope.launch {
                musicDataStore.updateMusicList(musicLists, id)
            }
        }
        if (currentMusicIndex != musicIndex) {
            viewModelScope.launch {
                musicDataStore.updateMusicIndex(musicIndex)
            }
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

    fun getCurrentMusicPosition(): Int {
        return currentMusicIndex ?: 0
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

    fun playOrPause() {
        viewModelScope.launch {
            val lastState = _musicStateStateFlow.value
            musicDataStore.updateMusicState(
                MusicState(
                    musicIsPlaying = lastState.musicIsPlaying.not(),
                    musicPosition = lastState.musicPosition,
                )
            )
        }
    }
}
