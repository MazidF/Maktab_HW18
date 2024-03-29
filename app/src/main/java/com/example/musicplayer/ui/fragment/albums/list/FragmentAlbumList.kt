package com.example.musicplayer.ui.fragment.albums.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.R
import com.example.musicplayer.data.model.AlbumInfo
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.FragmentAlbumListBinding
import com.example.musicplayer.ui.fragment.FragmentWithOnBackListener
import com.example.musicplayer.ui.fragment.albums.ViewModelAlbums
import com.example.musicplayer.utils.Mapper.toSelectableMusic
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAlbumList : FragmentWithOnBackListener(R.layout.fragment_album_list) {
    private val args: FragmentAlbumListArgs by navArgs()
    private lateinit var albumAdapter: MusicAlbumsItemAdapter

    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelAlbumList by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAlbumListBinding.bind(view)
        init()
        observer()
    }

    private fun init() = with(binding) {
        albumAdapter = MusicAlbumsItemAdapter(
            onItemClick = this@FragmentAlbumList::onClick,
            onMoreClick = this@FragmentAlbumList::onMoreClick,
            viewLifecycleOwner
        )
        albumList.adapter = albumAdapter
    }

    private fun onClick(music: Music, pos: Int) {

    }
    private fun onMoreClick(music: Music) {

    }

    private fun observer() {
        viewModel.getMusicList(args.album.id).observe(viewLifecycleOwner) {
            albumAdapter.submitList(it)
        }
    }

    override fun onBackPressed(): Boolean {
        return albumAdapter.clearSelection()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}