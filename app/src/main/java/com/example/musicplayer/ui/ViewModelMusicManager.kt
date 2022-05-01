package com.example.musicplayer.ui

import androidx.lifecycle.ViewModel
import com.example.musicplayer.domain.MusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelMusicManager @Inject constructor(
    private val useCase: MusicUseCase
) : ViewModel() {

}