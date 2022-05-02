package com.example.musicplayer.ui.fragment.tracks

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentTracksBinding
import com.example.musicplayer.ui.fragment.AlphabetAdapter
import com.example.musicplayer.utils.isLandScape
import com.example.musicplayer.utils.repeatLaunchOnState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentTracks : Fragment(R.layout.fragment_tracks) {

    private lateinit var musicAdapter: MusicTracksItemAdapter

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelTracks by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTracksBinding.bind(view)
        init()
        observer()
    }

    private fun init() = with(binding) {
        musicAdapter = MusicTracksItemAdapter()
        trackList.adapter = musicAdapter
        trackScrollbar.apply {
            alphabetSeekbar.apply {
                alphabetList.viewTreeObserver.addOnGlobalLayoutListener(object :
                    OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        alphabetList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        alphabetSeekbar.layoutParams = FrameLayout.LayoutParams(alphabetList.height, alphabetList.width)
                        val centreX=alphabetList.x + alphabetList.width / 2
                        val centreY=alphabetList.y + alphabetList.height / 2
                        alphabetSeekbar.animate().rotation(90f).x(centreX + 30).y(centreY - 30)
                    }
                })
            }
            alphabetList.apply {
                val list = getAlphabet()
                adapter = AlphabetAdapter(list)
                layoutManager = GridLayoutManager(requireContext(), list.size).apply {
                    orientation = GridLayoutManager.HORIZONTAL
                }
            }
        }
    }

    private fun getAlphabet(): List<Char> {
        return if (requireContext().isLandScape()) {
            ('A' .. 'Z' step 4).toList() + listOf('Z')
        } else {
            ('A' .. 'Z').toList()
        }
    }

    private fun observer() = with(binding) {
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            viewModel.musicsStateFlow.collect {
                musicAdapter.submitList(it)
                trackScrollbar.alphabetSeekbar.apply {
                    max = it.size
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}