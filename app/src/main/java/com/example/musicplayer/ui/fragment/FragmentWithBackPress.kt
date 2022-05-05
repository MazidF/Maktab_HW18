package com.example.musicplayer.ui.fragment

import androidx.fragment.app.Fragment

abstract class FragmentWithBackPress : Fragment {
    constructor() : super()
    constructor(resource: Int) : super(resource)

    abstract fun handleOnBackPressed(): Boolean
}