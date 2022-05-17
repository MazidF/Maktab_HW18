package com.example.musicplayer.ui.fragment.music_viewer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentMusicBinding
import com.example.musicplayer.ui.activity.main.ViewModelMain
import com.example.musicplayer.utils.repeatLaunchOnState
import com.example.musicplayer.utils.secondToTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FragmentMusicViewer : Fragment(R.layout.fragment_music) {
    private var _binding: FragmentMusicBinding? = null
    private val binding: FragmentMusicBinding
        get() = _binding!!

    private val viewModel: ViewModelMain by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMusicBinding.bind(view)
        init()
        observe()
    }

    private fun setupShuffle(hasShuffle: Boolean) {
        binding.musicViewerShuffle.setImageResource(
            if (hasShuffle) R.drawable.ic_shuffle else R.drawable.ic_right
        )
    }

    private fun init() = with(binding) {
        musicViewerName.isSelected = true
        musicViewerArtist.isSelected = true
        musicViewerDuration.text = viewModel.musicDuration().secondToTimeFormatter()
        setupShuffle(viewModel.getShuffle())
        musicViewerPrev.setOnClickListener {
            viewModel.prev()
        }
        musicViewerPlayOrPause.setOnClickListener {
            viewModel.playOrPause()
        }
        musicViewerNext.setOnClickListener {
            viewModel.next()
        }
        musicViewerShuffle.setOnClickListener {
            viewModel.shuffle()
        }
        musicViewerSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, pos: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.seekTo(pos)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setupSeekBar(duration: Int) = with(binding) {
        musicViewerSeekbar.apply {
            max = duration
        }
        musicViewerCurrentTime.text = "0:00"
        musicViewerDuration.text = duration.secondToTimeFormatter()
    }


    // TODO: use just one repeatLaunchOnState() and use multi launch
    private fun observe() {
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            viewModel.currentMusicStateFlow.collect { music ->
                with(binding) {
                    musicViewerName.text = music.name
                    musicViewerArtist.text = viewModel.getArtist(music.artistId)?.name ?: ""
                    val image = music.getAlbumImage()
                    withContext(Dispatchers.Main) {
                        Glide.with(root)
                            .load(image)
                            .error(R.drawable.music_player_icon)
                            .into(musicViewerImage)
                    }
                }
            }
        }
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            viewModel.musicStateStateFlow.collect {
                val isPlaying = it.musicIsPlaying
                with(binding) {
                    musicViewerPlayOrPause.setImageResource(
                        if (isPlaying.not()) R.drawable.ic_play else R.drawable.ic_pause
                    )
                    if (isPlaying) {
                        musicViewerImage.animate().scaleX(1.5f).scaleY(1.5f).duration = 500
                    } else {
                        musicViewerImage.animate().scaleX(2 / 3f).scaleY(2 / 3f).duration = 500
                    }
                    var y = musicViewerImage.height
                    if (y != 0) {
                        if (isPlaying) {
                            y = y * 3 / 2
                        }
                        linearLayoutTexts.animate().y(musicViewerImage.y + y).duration = 510
                    }
                }
            }
        }
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            viewModel.hasShuffleStateFlow.collect { hasShuffle ->
                setupShuffle(hasShuffle)
            }
        }
        val seekbarFlow = viewModel.syncSeekbar()
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            with(binding) {
                seekbarFlow.collect { pos ->
                    withContext(Dispatchers.Main) {
                        musicViewerSeekbar.progress = pos
                        musicViewerCurrentTime.text = pos.secondToTimeFormatter()
                    }
                }
            }
        }
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            viewModel.musicDurationStateFlow.collect {
                setupSeekBar(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.show()
    }
}