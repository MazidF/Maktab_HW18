package com.example.musicplayer.ui.fragment.music_viewer

import android.annotation.SuppressLint
import android.graphics.Color.RED
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
import com.example.musicplayer.ui.ViewModelApp
import com.example.musicplayer.ui.activity.main.MainActivity
import com.example.musicplayer.utils.repeatLaunchOnState
import com.example.musicplayer.utils.secondToTimeFormatter


class FragmentMusicViewer : Fragment(R.layout.fragment_music) {
    private var _binding: FragmentMusicBinding? = null
    private val binding: FragmentMusicBinding
        get() = _binding!!

    private val viewModel: ViewModelApp by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMusicBinding.bind(view)
        observe()
        init()
    }

    private fun setupShuffle(hasShuffle: Boolean) = with(binding.musicViewerShuffle) {
        setColorFilter(
            if (hasShuffle) RED else context.getColor(R.color.base_icon_color)
        )
    }

    private fun init() = with(binding) {
        musicViewerName.isSelected = true
        musicViewerArtist.isSelected = true

        val binder = MainActivity.getBinder()
        setupShuffle(binder?.getShuffle() ?: false)
        musicViewerPrev.setOnClickListener {
            binder?.prev()
        }
        musicViewerPlayOrPause.setOnClickListener {
            binder?.playOrPause()
        }
        musicViewerNext.setOnClickListener {
            binder?.next()
        }
        musicViewerShuffle.setOnClickListener {
            binder?.shuffle()
        }
        musicViewerSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, pos: Int, fromUser: Boolean) {
                if (fromUser) {
                    binder?.seekTo(pos)
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
            viewModel.musicStateFlow.collect { music ->
                with(binding) {
                    musicViewerName.text = music.name
                    musicViewerArtist.text = viewModel.getArtist(music.artistId)?.name ?: ""
                    Glide.with(root)
                        .load(music.getAlbumImage())
                        .error(R.drawable.music_player_icon)
                        .into(musicViewerImage)
                }
            }
        }
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            viewModel.musicIsPlayingStateFlow.collect { isPlaying ->
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
            viewModel.musicHasShuffleStateFlow.collect { hasShuffle ->
                setupShuffle(hasShuffle)
            }
        }
        val seekbarFlow = MainActivity.getBinder()?.syncSeekbar()
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            with(binding) {
                seekbarFlow?.collect { pos ->
                    musicViewerSeekbar.progress = pos
                    musicViewerCurrentTime.text = pos.secondToTimeFormatter()
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