package com.example.musicplayer.ui.activity.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.musicplayer.R
import com.example.musicplayer.data.local.data_store.music.MusicDataStore
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.service.MusicService
import com.example.musicplayer.service.ServiceControlInput
import com.example.musicplayer.ui.ViewModelApp
import com.example.musicplayer.utils.gone
import com.example.musicplayer.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val navController by lazy {
        findNavController(R.id.fragmentContainerView)
    }
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

    private fun startService() {
        val intent = Intent(this, MusicService::class.java)
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }*/
        startService(intent)
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
            musicController.setOnClickListener {
                musicController.gone()
                navController.navigate(R.id.fragmentMusicViewer)
            }
        }
    }

    private fun observe() {
        appViewModel.hasSplashEnded.observe(this) {
            if (it == true) {
                startService()
                supportActionBar?.show()
                binding.musicController.visible()
                appViewModel.hasSplashEnded.removeObservers(this)
            } else if (it == false) {
                binding.musicController.gone()
                supportActionBar?.hide()
            }
        }
        lifecycleScope.launch {
            viewModel.currentMusicStateFlow.collect {
                if (binding.musicController.isVisible) {
                    binding.musicController.setMusic(
                        music = it,
                        artist = viewModel.getArtist(it.artistId)?.name ?: ""
                    )
                }
            }
        }
        lifecycleScope.launch {
            viewModel.musicStateStateFlow.collect {
                binding.musicController.setIsPlaying(it.musicIsPlaying)
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.musicController.isGone) {
            binding.musicController.visible()
            viewModel.currentMusicStateFlow.value.let {
                binding.musicController.setMusic(
                    music = it,
                    artist = viewModel.getArtist(it.artistId)?.name ?: ""
                )
            }
        }
    }

    private fun startNotification() {
        val intent = Intent(this, MusicService::class.java).apply {
            action = ServiceControlInput.START.name
        }
        startService(intent)
    }

    override fun onStop() {
        super.onStop()
        if (viewModel.isPlaying.value == true) {
            startNotification()
        } else {
            val intent = Intent(this, MusicService::class.java)
            stopService(intent)
        }
    }

    inner class MusicServiceConnection : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder) {

        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
        fun getStarterIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}