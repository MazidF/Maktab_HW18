package com.example.musicplayer.ui.activity.main


import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragmentStateAdapter<out T : Fragment>(
    activity: AppCompatActivity,
    private val fragments: List<Class<T>>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position].newInstance()
    }

}