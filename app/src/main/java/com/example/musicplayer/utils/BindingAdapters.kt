package com.example.musicplayer.utils

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("app:isVisible")
fun View.isVisible(isVisible: Boolean?) {
    this.isVisible = isVisible == true
}