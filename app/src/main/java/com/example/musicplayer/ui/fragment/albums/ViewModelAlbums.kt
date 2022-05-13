package com.example.musicplayer.ui.fragment.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.data.model.AlbumInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelAlbums @Inject constructor(
    private val useCase: MusicUseCase
): ViewModel() {

    val albumsInfoStateFlow = MutableStateFlow<List<AlbumInfo>>(emptyList())

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
