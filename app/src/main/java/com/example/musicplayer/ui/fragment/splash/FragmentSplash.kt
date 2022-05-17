package com.example.musicplayer.ui.fragment.splash

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.dataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.musicplayer.R
import com.example.musicplayer.data.local.data_store.main.MainDataStore
import com.example.musicplayer.databinding.FragmentSplashBinding
import com.example.musicplayer.domain.MusicUseCase
import com.example.musicplayer.ui.ViewModelApp
import com.example.musicplayer.utils.repeatLaunchOnState
import com.example.musicplayer.workmanager.LoadMusicWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class FragmentSplash : Fragment(R.layout.fragment_splash) {
    private val navController by lazy {
        findNavController()
    }
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private var hasBeenLoaded = MutableLiveData<Boolean>()

    private val permission = Manifest.permission.READ_EXTERNAL_STORAGE

    private val appViewModel: ViewModelApp by activityViewModels()

    @Inject
    lateinit var useCase: MusicUseCase

    @Inject
    lateinit var mainDatastore: MainDataStore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)
        observer()
    }

    private fun observer() {
        repeatLaunchOnState(Lifecycle.State.STARTED) {
            mainDatastore.preferences.collect {
                hasBeenLoaded.value = it.musicHasBeenLoaded
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (appViewModel.hasSplashEnded.value != true) {
            useCase // calling the object to make sure init {} will be called
            binding.root.animate().alpha(1f).withEndAction {
                thread {
                    Thread.sleep(1000)
                    permissionLauncher.launch(permission)
                }
            }.duration = 1500
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                onPermissionResult(it)
            }
    }

    private fun onPermissionResult(hasAccess: Boolean) {
        if (hasAccess) {
            hasBeenLoaded.observe(viewLifecycleOwner) {
                if (it == true) {
                    navigate()
                } else if (it == false) {
                    loadMusics()
                }
            }
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun loadMusics() {
        val request: WorkRequest = OneTimeWorkRequestBuilder<LoadMusicWorker>().build()
        WorkManager.getInstance(requireContext()).enqueue(request)
    }

    private fun navigate() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.fragmentSplash, true)
            .build()
        navController.navigate(
            FragmentSplashDirections.actionFragmentSplashToFragmentMain(),
            navOptions = navOptions
        )
        appViewModel.hasSplashEnded.value = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}