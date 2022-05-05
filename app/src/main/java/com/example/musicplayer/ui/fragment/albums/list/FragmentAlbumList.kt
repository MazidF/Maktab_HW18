package com.example.musicplayer.ui.fragment.albums.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentAlbumListBinding
import com.example.musicplayer.ui.fragment.FragmentWithOnBackListener
import com.example.musicplayer.utils.Mapper.toSelectableMusic

class FragmentAlbumList : FragmentWithOnBackListener(R.layout.fragment_album_list) {
    private val args: FragmentAlbumListArgs by navArgs()
    private lateinit var albumAdapter: MusicAlbumsItemAdapter
    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAlbumListBinding.bind(view)
        init()
        observer()
    }

    private fun init() = with(binding) {
        albumAdapter = MusicAlbumsItemAdapter()
        albumList.adapter = albumAdapter
        albumAdapter.submitList(args.info.musics.map {
            it.toSelectableMusic()
        })
    }

    private fun observer() {}

    override fun onBackPressed(): Boolean {
        if (albumAdapter.isSelecting()) {
            albumAdapter.removeSelection()
            return true
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}