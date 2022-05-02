package com.example.musicplayer.ui.activity.loading

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.musicplayer.databinding.ActivityLoadingBinding
import com.example.musicplayer.ui.activity.main.MainActivity
import kotlin.concurrent.thread

class LoadingActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: ActivityLoadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            hide()
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            onPermissionResult(it)
        }
    }

    private fun onPermissionResult(hasAccess: Boolean) {
        if (hasAccess) {
            startActivity(MainActivity.getStarterIntent(this))
            finish()
        } else {
            // TODO: show a alert dialog
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.animate().alpha(1f).withEndAction {
            thread {
                Thread.sleep(1000)
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }.duration = 2000
    }

}