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
            onItemClick = this@FragmentAlbumList::onClick
        )
        albumList.adapter = albumAdapter
    }

    private fun onClick(music: Music) {

    }

    private fun observer() {
        viewModel.getMusicList(args.album.id).observe(viewLifecycleOwner) {
            albumAdapter.submitList(it)
        }
    }

    override fun onBackPressed(): Boolean {
        // TODO: clear selection state if needed
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}