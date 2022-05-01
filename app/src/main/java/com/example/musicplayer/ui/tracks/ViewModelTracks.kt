package com.example.musicplayer.ui.tracks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelTracks @Inject constructor(
    private val repository: MusicRepository
): ViewModel() {

    private val _musicStateFlow = MutableStateFlow(emptyList<Music>())
    val musicStateFlow = _musicStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _musicStateFlow.first()
        }
    }

    fun getMusics(context: Context?): Flow<List<Music>> {
        return repository.getAllMusics(context)
    }
}
