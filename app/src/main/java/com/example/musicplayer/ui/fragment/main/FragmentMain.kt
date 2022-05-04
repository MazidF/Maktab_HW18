package com.example.musicplayer.ui.fragment.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentMainBinding
import com.example.musicplayer.ui.activity.main.MyFragmentStateAdapter
import com.example.musicplayer.ui.activity.main.ZoomOutPageTransformer
import com.example.musicplayer.ui.fragment.albums.FragmentAlbums
import com.example.musicplayer.ui.fragment.tracks.FragmentTracks
import com.google.android.material.tabs.TabLayoutMediator

class FragmentMain : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        init()
    }

    private fun init() = with(binding) {
        viewpager.apply {
            val list = listOf<Fragment>(
                FragmentTracks::class.java.newInstance(),
                FragmentAlbums::class.java.newInstance()
            )
            adapter = MyFragmentStateAdapter(
                this@FragmentMain,
                fragments = list
            )
            setPageTransformer(ZoomOutPageTransformer())
        }
        val tabNames = resources.getStringArray(R.array.items)
        TabLayoutMediator(tab, viewpager) { tab, pos ->
            tab.text = tabNames[pos]
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}