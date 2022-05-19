package com.example.musicplayer.ui.fragment.tracks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.musicplayer.R
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.FragmentTracksBinding
import com.example.musicplayer.ui.ViewModelApp
import com.example.musicplayer.ui.activity.main.MainActivity
import com.example.musicplayer.ui.fragment.FragmentWithBackPress
import com.example.musicplayer.utils.createAlphabetScrollbar
import com.example.musicplayer.utils.smoothSnapToPosition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentTracks : FragmentWithBackPress(R.layout.fragment_tracks) {

    private lateinit var musicAdapter: MusicTracksItemAdapter

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelTracks by viewModels()
    private val appViewModel: ViewModelApp by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTracksBinding.bind(view)
        init()
        observer()
    }

    private fun init() = with(binding) {
        musicAdapter = MusicTracksItemAdapter(
            artistList = viewModel.artists(),
            onItemClick = this@FragmentTracks::onClick,
            onMoreClick = this@FragmentTracks::onMoreClick,
            lifecycleOwner = viewLifecycleOwner
        ).apply {
            isSelected.observe(viewLifecycleOwner) {
                appViewModel.startSelection()
            }
        }
        trackList.apply {
            adapter = musicAdapter
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

    private fun onClick(music: Music, pos: Int) {
        MainActivity.getBinder()?.changeList(MusicLists.TRACKS(), pos)
    }

    private fun onMoreClick(music: Music) {

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
}