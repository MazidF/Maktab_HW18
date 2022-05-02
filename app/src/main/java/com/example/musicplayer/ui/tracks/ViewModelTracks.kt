package com.example.musicplayer.ui.tracks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.utils.StateFlowWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelTracks @Inject constructor(
    private val useCase: MusicUseCase
): ViewModel() {

    init {
        getMusics()
    }

    val musicsStateFlow = StateFlowWrapper<List<Music>>(emptyList())

    private fun getMusics() {
        viewModelScope.launch {
            useCase.musicStateFlow.collect {
                musicsStateFlow.emit(it)
            }
        }
    }
}
