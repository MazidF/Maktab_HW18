package com.example.musicplayer.ui.fragment.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.musicplayer.R
import com.example.musicplayer.data.local.data_store.music.MusicLists
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.FragmentFavoriteBinding
import com.example.musicplayer.ui.ViewModelApp
import com.example.musicplayer.ui.activity.main.MainActivity
import com.example.musicplayer.ui.fragment.FragmentWithBackPress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentFavorite : FragmentWithBackPress(R.layout.fragment_favorite) {
    private lateinit var musicAdapter: MusicFavoritesItemAdapter
    private val viewModel: ViewModelFavorite by viewModels()
    private val appViewModel: ViewModelApp by activityViewModels()

    private var _binding: FragmentFavoriteBinding? = null
    private val binding: FragmentFavoriteBinding
        get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)
        init()
        observe()
    }

    private fun init() = with(binding) {
        musicAdapter = MusicFavoritesItemAdapter(
            artistList = viewModel.artists(),
            onItemClick = this@FragmentFavorite::onClick,
            onMoreClick = this@FragmentFavorite::onMore,
            lifecycleOwner = viewLifecycleOwner,
        ).apply {
            isSelected.observe(viewLifecycleOwner) {
                appViewModel.startSelection()
            }
        }
        favoriteList.apply {
            adapter = musicAdapter
        }
    }

    private fun onClick(music: Music, pos: Int) {
        MainActivity.getBinder()?.changeList(MusicLists.FAVORITES(), pos)
    }

    private fun onMore(music: Music) {

    }

    private fun observe() = with(binding) {
        viewModel.musicList.observe(viewLifecycleOwner) {
            musicAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun handleOnBackPressed(): Boolean {
        return musicAdapter.clearSelection()
    }
}