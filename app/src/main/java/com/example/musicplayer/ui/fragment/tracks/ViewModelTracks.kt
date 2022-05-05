package com.example.musicplayer.ui.fragment.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.ui.model.SelectableMusic
import com.example.musicplayer.utils.Mapper.toSelectableMusic
import com.example.musicplayer.utils.StateFlowWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelTracks @Inject constructor(
    private val useCase: MusicUseCase
): ViewModel() {
    val musicsStateFlow = MutableStateFlow<List<SelectableMusic>>(emptyList())

    init {
        getMusics()
    }

    private fun getMusics() {
        viewModelScope.launch {
            useCase.musicStateFlow.collect { list ->
                val result =list.map {
                    it.toSelectableMusic()
                }
                musicsStateFlow.emit(result)
            }
        }
    }
}
