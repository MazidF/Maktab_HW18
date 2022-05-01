package com.example.musicplayer.ui.tracks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentTracksBinding
import com.example.musicplayer.utils.LifeCycleAwareBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentTracks : Fragment(R.layout.fragment_tracks) {

    private lateinit var musicAdapter: MusicTracksItemAdapter

    private val binding = LifeCycleAwareBinding<FragmentTracksBinding>(parentFragmentManager)()

    private val viewModel: ViewModelTracks by viewModels()

    var hasBeenLoaded: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        observer()
    }

    private fun init() = with(binding) {
        val context = if (hasBeenLoaded) null else requireContext()
        musicAdapter = MusicTracksItemAdapter()
        lifecycleScope.launch {
            viewModel.getMusics(context).collect {
                musicAdapter.addAndSubmitList(it)
            }
            hasBeenLoaded = true
        }
        binding.list.adapter = musicAdapter
    }

    private fun observer() = with(binding) {
        lifecycleScope.launch {
//            viewModel.getMusics()
        }
    }
}