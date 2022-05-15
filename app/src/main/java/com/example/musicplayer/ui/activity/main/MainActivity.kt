package com.example.musicplayer.ui.activity.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.data.local.data_store.music.MusicDataStore
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.ui.ViewModelApp
import com.example.musicplayer.utils.gone
import com.example.musicplayer.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ViewModelMain by viewModels()
    private val appViewModel: ViewModelApp by viewModels()

    @Inject
    lateinit var musicDataStore: MusicDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        observe()
    }

    private fun init() = with(binding) {
        with(musicController) {
            setOnNextClickListener {
                viewModel.next()
            }
            setOnPrevClickListener {
                viewModel.prev()
            }
            setOnPausePlayClickListener {
                viewModel.playOrPause()
            }
        }
    }

    private fun observe() {
        appViewModel.hasSplashEnded.observe(this) {
            if (it == true) {
                supportActionBar?.show()
                binding.musicController.visible()
                appViewModel.hasSplashEnded.removeObservers(this)
            } else if (it == false) {
                binding.musicController.gone()
                supportActionBar?.hide()
            }
        }

        // TODO: move this code to hasSplashEnded.observer if needed
        lifecycleScope.launch {
            viewModel.currentMusicStateFlow.collect {
                binding.musicController.setMusic(
                    music = it,
                    artist = viewModel.getArtist(it.artistId)?.name ?: ""
                )
            }
        }
    }

    companion object {
        fun getStarterIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}