package com.example.musicplayer.ui.fragment.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelFavorite @Inject constructor(
    private val useCase: MusicUseCase,
) : ViewModel() {

    fun artists() = useCase.artistMapStateFlow.value

    val musicList: LiveData<PagedList<Music>> =
        useCase.getFavoritesPaging().toLiveData(Constants.MUSIC_PER_PAGE)
}
