package com.example.musicplayer.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.local.data_store.music.MusicDataStore
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.domain.MusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    // TODO: methods for selection, hiding controller and ...

    fun updateMusicLists(musicLists: MusicLists, id: Long?, position: Int) {
        viewModelScope.launch {
            musicDataStore.updateMusicList(musicLists, id)
            musicDataStore.updateMusicIndex(position)
        }
    }
}