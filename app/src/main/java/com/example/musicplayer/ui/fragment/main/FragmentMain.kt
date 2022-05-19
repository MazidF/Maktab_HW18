package com.example.musicplayer.ui.fragment.main

import android.os.Bundle
import android.view.View
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentMainBinding
import com.example.musicplayer.ui.activity.main.MyFragmentStateAdapter
import com.example.musicplayer.ui.activity.main.ZoomOutPageTransformer
import com.example.musicplayer.ui.fragment.FragmentWithBackPress
import com.example.musicplayer.ui.fragment.FragmentWithOnBackListener
import com.example.musicplayer.ui.fragment.albums.FragmentAlbums
import com.example.musicplayer.ui.fragment.favorite.FragmentFavorite
import com.example.musicplayer.ui.fragment.tracks.FragmentTracks
import com.google.android.material.tabs.TabLayoutMediator

class FragmentMain : FragmentWithOnBackListener(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val fragments = listOf<FragmentWithBackPress>(
        FragmentFavorite::class.java.newInstance(),
        FragmentTracks::class.java.newInstance(),
        FragmentAlbums::class.java.newInstance(),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        init()
    }

    private fun init() = with(binding) {
        viewpager.apply {
            adapter = MyFragmentStateAdapter(
                this@FragmentMain,
                fragments = fragments
            )
            setPageTransformer(ZoomOutPageTransformer())
        }
        val tabNames = resources.getStringArray(R.array.items)
        TabLayoutMediator(tab, viewpager) { tab, pos ->
            tab.text = tabNames[pos]
        }.attach()
    }

    override fun onBackPressed(): Boolean {
        var result = true
        for (fragment in fragments) {
            result = result.and(fragment.handleOnBackPressed())
        }
        return result
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}