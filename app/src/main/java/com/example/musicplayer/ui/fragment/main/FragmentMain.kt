package com.example.musicplayer.ui.fragment.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentMainBinding
import com.example.musicplayer.ui.activity.main.MyFragmentStateAdapter
import com.example.musicplayer.ui.activity.main.ZoomOutPageTransformer
import com.example.musicplayer.ui.fragment.FragmentWithBackPress
import com.example.musicplayer.ui.fragment.FragmentWithOnBackListener
import com.example.musicplayer.ui.fragment.albums.FragmentAlbums
import com.example.musicplayer.ui.fragment.tracks.FragmentTracks
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

class FragmentMain : FragmentWithOnBackListener(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val fragments = listOf<FragmentWithBackPress>(
        FragmentTracks::class.java.newInstance(),
        FragmentAlbums::class.java.newInstance()
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