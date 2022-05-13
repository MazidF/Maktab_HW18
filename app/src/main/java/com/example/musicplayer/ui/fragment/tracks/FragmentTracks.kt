package com.example.musicplayer.ui.fragment.tracks

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.FragmentTracksBinding
import com.example.musicplayer.ui.fragment.FragmentWithBackPress
import com.example.musicplayer.ui.selection.createSelectionTracker
import com.example.musicplayer.utils.createAlphabetScrollbar
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
        ).also {
            it.isSelecting.observe(viewLifecycleOwner) { bool ->
                Toast.makeText(requireContext(), bool.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        trackList.apply {
            adapter = musicAdapter
            musicAdapter.setTracker(createSelectionTracker(
                id = SELECTION_ID,
                recyclerView = this
            ))
        }
        scrollbarInit()
    }

    private fun scrollbarInit() = with(binding) {
        createAlphabetScrollbar(trackScrollbar) { char ->
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
/*        if (musicAdapter.clearSelection()) {
            return true
        }
        return false*/
        return musicAdapter.clearSelection()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SELECTION_ID = "fragment_tracks_selection_id"
    }
}