package com.example.musicplayer.ui.fragment.tracks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionTracker
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.FragmentTracksBinding
import com.example.musicplayer.ui.fragment.FragmentWithBackPress
import com.example.musicplayer.utils.createAlphabetSeekbar
import com.example.musicplayer.utils.smoothSnapToPosition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentTracks : FragmentWithBackPress(R.layout.fragment_tracks) {

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
        musicAdapter = MusicTracksItemAdapter(
            artistList = viewModel.artists,
            onItemClick = this@FragmentTracks::onClick
        )
        trackList.adapter = musicAdapter
        // TODO: add selection tracker
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

    private fun onClick(music: Music) {

    }

    private fun observer() = with(binding) {
        viewModel.musicList.observe(viewLifecycleOwner) {
            musicAdapter.submitList(it)
        }
    }

    override fun handleOnBackPressed(): Boolean {
        // TODO: cancel selection if needed
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}