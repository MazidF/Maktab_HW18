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
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.service.ServiceControlInput
import com.example.musicplayer.service.SuperMusicService
import com.example.musicplayer.service.receiver.SCI
import com.example.musicplayer.ui.ViewModelApp
import com.example.musicplayer.ui.fragment.splash.FragmentSplashDirections
import com.example.musicplayer.utils.gone
import com.example.musicplayer.utils.isServiceActive
import com.example.musicplayer.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val navController by lazy {
        findNavController(R.id.fragmentContainerView)
    }
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ViewModelApp by viewModels()

    private val connection by lazy {
        MusicServiceConnection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isServiceActive(SuperMusicService::class.java)) {
            viewModel.hasSplashEnded.value = null
            startService(SCI.END_NOTIFICATION.name)
            navigateToMainFragment(savedInstanceState != null)
        }
        init()
        observe()
    }

    private fun navigateToMainFragment(isRotation: Boolean) {
        if (isRotation.not()) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragmentSplash, true)
                .build()
            navController.navigate(
                FragmentSplashDirections.actionFragmentSplashToFragmentMain(),

                navOptions
            )
        }
    }

    private fun startService(action: String? = null) {
        val intent = SuperMusicService.getIntent(this).apply {
            this.action = action
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && action == null) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun init() = with(binding) {
        with(musicController) {
            setOnNextClickListener {
                connection.binder?.next()
            }
            setOnPrevClickListener {
                connection.binder?.prev()
            }
            setOnPausePlayClickListener {
                connection.binder?.playOrPause()
            }
            musicController.setOnClickListener {
                musicController.gone()
                navController.navigate(R.id.fragmentMusicViewer)
            }
        }
    }

    private fun observe() {
        viewModel.hasSplashEnded.observe(this) {
            if (it == true) {
                startService()
                supportActionBar?.show()
                binding.musicController.visible()
                viewModel.hasSplashEnded.removeObservers(this)
            } else if (it == false) {
                binding.musicController.gone()
                supportActionBar?.hide()
            }
        }
        lifecycleScope.launch {
            viewModel.musicStateFlow.collect {
                if (binding.musicController.isVisible) {
                    binding.musicController.setMusic(
                        music = it,
                        artist = viewModel.getArtist(it.artistId)?.name ?: ""
                    )
                }
            }
        }
        lifecycleScope.launch {
            viewModel.musicIsPlayingStateFlow.collect {
                binding.musicController.setIsPlaying(it)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.musicController.isGone) {
            binding.musicController.visible()
            viewModel.musicStateFlow.value.let {
                binding.musicController.setMusic(
                    music = it,
                    artist = viewModel.getArtist(it.artistId)?.name ?: ""
                )
            }
        }
    }

    private fun startNotification() {
        val intent = SuperMusicService.getIntent(this).apply {
            action = ServiceControlInput.START_NOTIFICATION.name
        }
        startService(intent)
    }

    override fun onStop() {
        super.onStop()
        if (connection.binder?.isPlaying() == true) {
            startNotification()
        } else {
            val intent = SuperMusicService.getIntent(this).apply {
                action = SCI.END_SERVICE.name
            }
            startService(intent)
            // or stopService(intent)
        }
    }

    inner class MusicServiceConnection : ServiceConnection {
        var binder: SuperMusicService.MusicBinder? = null
            set(value) {
                MainActivity.binder = value
                field = value
            }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder) {
            this.binder = binder as SuperMusicService.MusicBinder
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            binder = null
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        private var binder: SuperMusicService.MusicBinder? = null
        fun getBinder() = binder
    }
}