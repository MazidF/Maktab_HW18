package com.example.musicplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.local.data_store.music.MusicDataStore
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.domain.MusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelApp @Inject constructor(
    private val useCase: MusicUseCase,
    private val musicDataStore: MusicDataStore
) : ViewModel() {
    val hasSplashEnded by lazy {
        MutableLiveData(false)
    }

    private val _musicStateFlow = useCase.currentMusicStateFlow
    val musicStateFlow get() = _musicStateFlow.asStateFlow()

    private val _musicDurationStateFlow = useCase.currentMusicDurationStateFlow
    val musicDurationStateFlow get() = _musicDurationStateFlow.asStateFlow()

    private val _musicIsPlayingStateFlow = useCase.currentMusicIsPlayingStateFlow
    val musicIsPlayingStateFlow get() = _musicIsPlayingStateFlow.asStateFlow()


    private val _musicHasShuffleStateFlow = useCase.currentMusicHasShuffleStateFlow
    val musicHasShuffleStateFlow get() = _musicHasShuffleStateFlow.asStateFlow()


    fun getArtist(artistId: Long): Artist? {
        return useCase.artistMapStateFlow.value[artistId]
    }


    // TODO: methods for selection, hiding controller and ...
    private val _selection by lazy {
        MutableLiveData(false)
    }
    val selection: LiveData<Boolean> get() = _selection

    private val _selectionCount by lazy {
        MutableLiveData(-1)
    }
    val selectionCount: LiveData<Int> get() = _selectionCount

    fun startSelection() {
        _selection.value = true
    }

    fun selectionCount(count: Int) {
        _selectionCount.value = count
    }

    fun endSelection() {
        _selection.value = false
    }
}