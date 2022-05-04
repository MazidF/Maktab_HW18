package com.example.musicplayer.ui.fragment

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlphabetAdapter(
    private val alphabet: List<Char>
) : RecyclerView.Adapter<AlphabetAdapter.CharHolder>() {

    @SuppressLint("ClickableViewAccessibility")
    inner class CharHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        init {
            textView.apply {
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }
        }

        fun bind(item: Char) {
            textView.text = item.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharHolder {
        return CharHolder(TextView(parent.context))
    }

    override fun onBindViewHolder(holder: CharHolder, position: Int) {
        holder.bind(alphabet[position])
    }

    override fun getItemCount() = alphabet.size
}
