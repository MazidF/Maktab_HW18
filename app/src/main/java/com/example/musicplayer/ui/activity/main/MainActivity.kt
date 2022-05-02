package com.example.musicplayer.ui.activity.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.ui.fragment.tracks.FragmentTracks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() = with(binding) {
        viewpager.apply {
            this.adapter = MyFragmentStateAdapter(this@MainActivity, listOf(FragmentTracks::class.java))
        }
    }

    companion object {
        fun getStarterIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}