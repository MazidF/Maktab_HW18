package com.example.musicplayer.ui.fragment.tracks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentTracksBinding
import com.example.musicplayer.utils.createAlphabetSeekbar
import com.example.musicplayer.utils.repeatLaunchOnState
import com.example.musicplayer.utils.smoothSnapToPosition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentTracks : Fragment(R.layout.fragment_tracks) {

    private lateinit var musicAdapter: MusicTracksItemAdapter

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelTracks by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTracksBinding.bind(view)
        init()
        observer()
    }

    private fun init() = with(binding) {
        musicAdapter = MusicTracksItemAdapter()
        trackList.adapter = musicAdapter
        createAlphabetSeekbar(trackScrollbar) { char ->
            musicAdapter.scrollToFirst({
                it.name.uppercase().first() == char
            }) {
                if (it != -1) {
                    trackList.smoothSnapToPosition(it)
                }
            }
        }
    }

    private fun observer() = with(binding) {
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            viewModel.musicsStateFlow.collect {
                musicAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}