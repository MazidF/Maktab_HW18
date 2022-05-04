package com.example.musicplayer.ui.fragment.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.ui.model.AlbumInfo
import com.example.musicplayer.utils.StateFlowWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelAlbums @Inject constructor(
    private val useCase: MusicUseCase
): ViewModel() {

    val albumsInfoStateFlow = StateFlowWrapper<List<AlbumInfo>>(emptyList())

    init {
        getAlbumsInfo()
    }

    private fun getAlbumsInfo() {
        viewModelScope.launch {
            useCase.getAlbumsInfo().collect {
                albumsInfoStateFlow.emit(it)
            }
        }
    }
}
