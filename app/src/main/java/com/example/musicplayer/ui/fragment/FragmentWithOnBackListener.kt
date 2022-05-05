package com.example.musicplayer.ui.fragment

import android.view.KeyEvent
import androidx.fragment.app.Fragment

abstract class FragmentWithOnBackListener : Fragment {
    constructor() : super()
    constructor(resource: Int) : super(resource)

    override fun onResume() {
        super.onResume()
        onBackPressedSetup()
    }

    private fun onBackPressedSetup() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                return@setOnKeyListener onBackPressed()
            }
            false
        }
    }

    abstract fun onBackPressed(): Boolean
}