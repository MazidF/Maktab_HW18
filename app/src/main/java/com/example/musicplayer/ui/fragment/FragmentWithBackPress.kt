package com.example.musicplayer.ui.fragment

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

abstract class FragmentWithBackPress : Fragment {
    constructor() : super()
    constructor(resource: Int) : super(resource)

    abstract fun handleOnBackPressed(): Boolean

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (this@FragmentWithBackPress.handleOnBackPressed().not()) {
                        activity?.onBackPressed()
                    }
                }
            }
        )
    }
}