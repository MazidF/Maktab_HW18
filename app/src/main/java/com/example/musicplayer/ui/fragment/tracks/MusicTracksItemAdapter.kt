package com.example.musicplayer.ui.fragment.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.MusicItemTracksBinding
import com.example.musicplayer.ui.fragment.MusicItemAdapter
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.set
import com.example.musicplayer.utils.setup
import com.example.musicplayer.utils.vibrate
import kotlinx.coroutines.*

class MusicTracksItemAdapter(
    private val artistList: HashMap<Long, Artist>,
    private val onItemClick: (Music, Int) -> Unit,
    private val onMoreClick: (Music) -> Unit,
    private val lifecycleOwner: LifecycleOwner
) : MusicItemAdapter<MusicTracksItemAdapter.MusicTracksHolder>() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val isSelected by lazy {
        MutableLiveData<Boolean>()
    }
    private var selections: HashSet<Long>? = null

    init {
        isSelected.observe(lifecycleOwner) {
            if (it == false) {
                selections = null
            }
        }
    }

    inner class MusicTracksHolder(
        private val binding: MusicItemTracksBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var music: Music? = null

        init {
            with(binding) {
                isSelected.observe(lifecycleOwner) {
                    val isActive = it == true
                    musicItemSelect.isVisible = isActive
                    musicItemMore.isVisible = isActive.not()
                    if (it == false) {
                        select(false)
                    }
                }
                root.setOnLongClickListener {
                    if (isSelected.value != true) {
                        selections = HashSet<Long>(20).apply {
                            music?.id?.let { id ->
                                add(id)
                            }
                        }
                        select(true)
                        root.context.vibrate()
                        isSelected.value = true
                    }
                    true
                }
                root.setOnClickListener {
                    selections?.let {
                        if (it.remove(music!!.id)) {
                            select(false)
                        } else {
                            select(it.add(music!!.id))
                        }
                        return@setOnClickListener
                    }
                    music?.run {
                        onItemClick(this, bindingAdapterPosition)
                    }
                }
                musicItemMore.setOnClickListener {

                }
                musicItemSelect.setOnClickListener {
                    musicItemSelect.setup {

                    }
                }
                // TODO: add onClickListener to radio_button
            }
        }

        fun selectionBind(music: Music, isSelected: Boolean) {
            select(isSelected)
            bind(music)
        }

        fun bind(music: Music) {
            this.music = music
            scope.launch {
                setMusic(music, artistList[music.artistId]?.name ?: "")
            }
        }

        private suspend fun setMusic(music: Music, artistName: String) = with(binding) {
            musicItemName.text = music.name
            musicItemArtist.text = artistName
            val icon = music.getAlbumImage()
            withContext(Dispatchers.Main) {
                Glide.with(root)
                    .applyDefaultRequestOptions(Constants.glideDiskCacheStrategy)
                    .load(icon)
                    .error(R.drawable.music_player_icon)
                    .into(musicItemImage)
            }
        }

        fun select(isSelected: Boolean) = with(binding) {
            musicItemSelect.set(isSelected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicTracksHolder {
        val binding = MusicItemTracksBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MusicTracksHolder(binding)
    }

    fun scrollToFirst(filter: (Music) -> Boolean, block: (Int) -> Unit) {
        val index = currentList?.indexOfFirst { music ->
            music?.let {
                filter(it)
            } ?: false
        } ?: return
        block(index)
    }

    override fun onBindViewHolder(holder: MusicTracksHolder, position: Int) {
        getItem(position)?.let { music ->
            selections?.let { set ->
                holder.selectionBind(music, set.contains(music.id))
            } ?: run {
                holder.bind(music)
            }
        }
    }

    fun clearSelection(): Boolean {
        if (isSelected.value == true) {
            isSelected.value = false
            return true
        }
        return false
    }
}