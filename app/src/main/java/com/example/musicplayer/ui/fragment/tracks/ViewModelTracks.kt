package com.example.musicplayer.ui.fragment.tracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.utils.Constants.MUSIC_PER_PAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelTracks @Inject constructor(
    private val useCase: MusicUseCase
): ViewModel() {
    fun artists() = useCase.artistMapStateFlow.value

    val musicList: LiveData<PagedList<Music>> = useCase.getTracksPaging().toLiveData(MUSIC_PER_PAGE)
}
