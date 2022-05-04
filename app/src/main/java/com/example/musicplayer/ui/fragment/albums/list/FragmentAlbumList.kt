package com.example.musicplayer.ui.fragment.albums.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentAlbumListBinding

class FragmentAlbumList : Fragment(R.layout.fragment_album_list) {
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
        albumAdapter.submitList(args.info.musics)
    }

    private fun observer() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}