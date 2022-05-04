package com.example.musicplayer.ui.activity.main


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragmentStateAdapter<out T : Fragment>(
    fragment: Fragment,
    private val fragments: List<T>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}