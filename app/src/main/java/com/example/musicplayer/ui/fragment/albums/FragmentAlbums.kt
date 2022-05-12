package com.example.musicplayer.ui.fragment.albums

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentAlbumsBinding
import com.example.musicplayer.ui.fragment.main.FragmentMainDirections
import com.example.musicplayer.data.model.AlbumInfo
import com.example.musicplayer.ui.fragment.FragmentWithBackPress
import com.example.musicplayer.utils.repeatLaunchOnState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAlbums : FragmentWithBackPress(R.layout.fragment_albums) {

    private val navController by lazy {
        findNavController()
    }

    private lateinit var albumInfoAdapter: AlbumItemAdapter

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelAlbums by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAlbumsBinding.bind(view)
        init()
        observer()
    }

    private fun init() = with(binding) {
        albumInfoAdapter = AlbumItemAdapter {
            onItemClick(it)
        }
        albumsList.apply {
            adapter = albumInfoAdapter
        }
    }

    private fun onItemClick(item: AlbumInfo) {
        navController.navigate(
            FragmentMainDirections.actionFragmentMainToFragmentAlbumList(item.album)
        )
    }

    private fun observer() = with(binding) {
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            viewModel.albumsInfoStateFlow.collect {
                albumInfoAdapter.submitList(it)
            }
        }
    }

    override fun handleOnBackPressed(): Boolean {
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}