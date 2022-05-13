package com.example.musicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.local.data_store.main.MainDataStore
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Music
import com.example.musicplayer.databinding.AlphabetSeekbarBinding
import com.example.musicplayer.ui.fragment.AlphabetAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap


typealias MediaInfo = MediaStore.Audio.Media

fun Int.timeFormatter(): String {
    val hour = this / (60 * 60)
    val min = this / 60
    val sec = this % 60
    return if (hour == 0) {
        "$min:${stringFormatter(sec)}"
    } else {
        "$hour:${stringFormatter(min)}:${stringFormatter(sec)}"
    }
}

fun Int.secondToTimeFormatter() = (this / 1000).timeFormatter()

fun stringFormatter(number: Int, length: Int = 2): String {
    return String.format("%0${length}d", number)
}

fun <T> Flow<T>.collectAsResult(): Flow<Result<T>> {
    return map { item ->
        Result.success(item)
    }.onStart {
        emit(Result.loading())
    }.catch { cause ->
        emit(Result.error(cause))
    }
}

infix fun <K, V> List<K>.to(other: List<V>): Array<Pair<K, V>> {
    return Array(size) {
        this[it] to other[it]
    }
}

fun <T, K> List<T>.toMap(key: (T) -> K): HashMap<K, T> {
    val keys = this.map(key)
    return hashMapOf(*keys to this)
}

fun Context.sharedPreferences(mode: Int = Context.MODE_PRIVATE): SharedPreferences {
    return getSharedPreferences(packageName, mode)
}

fun Context.hasBeenLoaded(): Boolean {
    return sharedPreferences().getBoolean(Constants.HAS_BEEN_LOADED, false)
}

fun Context.loaded() {
    return sharedPreferences().edit()
        .putBoolean(Constants.HAS_BEEN_LOADED, true)
        .apply()
}

fun AppCompatActivity.requestPermissions(
    failedCb: (List<String>) -> Unit
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        val failed = map.filterKeys {
            map[it] != true
        }
        failedCb(failed.keys.toList())
    }
}

fun <T> Flow<T>.collectAsList(size: Int): Flow<List<T>> = flow {
    var counter = 0
    val list = ArrayList<T>(size)
    this@collectAsList.collect {
        list.add(it)
        if (counter++ == size - 1) {
            emit(list.toList())
            counter = 0
            list.clear()
        }
    }
    emit(
        List(counter) {
            list[it]
        }
    )
}

suspend fun <T> Flow<T>.collectAndToList(
    initialCapacity: Int = 10,
    collector: FlowCollector<T>
): List<T> {
    val result = ArrayList<T>(initialCapacity)
    collect {
        collector.emit(it)
        result.add(it)
    }
    return result
}

fun shuffleIntArray(size: Int, random: Random = Random(System.currentTimeMillis())): List<Int> {
    return (0 until size).shuffled(random).toList()
}

fun Fragment.repeatLaunchOnState(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state, block)
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    var ob: Observer<T>? = null
    ob = Observer<T> {
        observer.onChanged(it)
        ob?.run {
            removeObserver(this)
        }
    }
    observeForever(ob)
}

fun Context.getOrientation(): Int {
    return resources.configuration.orientation
}

fun Context.isLandScape(): Boolean {
    return getOrientation() == Configuration.ORIENTATION_LANDSCAPE
}

fun RadioButton.setup(block: RadioButton.() -> Unit = {}) {
    set(false)
    this.setOnClickListener {
        if (isSelected) {
            isSelected = false
            isChecked = false
        } else {
            isSelected = true
            isChecked = true
        }
        block()
    }
}

fun RadioButton.set(value: Boolean) {
    isSelected = value
    isChecked = value
}

fun RecyclerView.smoothSnapToPosition(
    position: Int,
    snapMode: Int = LinearSmoothScroller.SNAP_TO_START
) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}

fun logger(msg: String, tag: String = "app_logger") {
    Log.d(tag, msg)
}

fun getAlphabet(context: Context): List<Char> {
    return if (context.isLandScape()) {
        ('A'..'Z' step 4).toList() + listOf('Z')
    } else {
        ('A'..'Z').toList()
    }
}

fun createAlphabetScrollbar(
    binding: AlphabetSeekbarBinding,
    cb: (Char) -> Unit
) {
    with(binding) {
        val context = root.context
        alphabetList.apply {
            val list = getAlphabet(context)
            adapter = AlphabetAdapter(list)
            layoutManager = GridLayoutManager(context, list.size).apply {
                orientation = GridLayoutManager.HORIZONTAL
            }
            alphabetSeekbar.visibleCallback = {
                alphabetName.isVisible = it
            }
            alphabetSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                val length by lazy {
                    alphabetSeekbar.height / (alphabetSeekbar.max + 1)
                }

                val minY by lazy {
                    alphabetList.y
                }

                val maxY by lazy {
                    minY + alphabetList.height
                }

                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(seekbar: SeekBar, progress: Int, fromUser: Boolean) {
                    val y =
                        seekbar.y + (progress * (alphabetSeekbar.height / (alphabetSeekbar.max + 1))) - alphabetName.height / 2
                    alphabetName.y = if (y > maxY) {
                        maxY
                    } else if (y < minY) {
                        minY
                    } else {
                        y
                    }
                    alphabetName.text = ('A' + progress).toString()
                    cb(list[progress]) // TODO: check why fromUser is always false
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    println()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    println()
                }

            })
        }

    }
}

fun Context.vibrate(duration: Long = 500): Boolean {
    val v = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        v.vibrate(duration)
    }
    return true
}

suspend fun pathToBitmap(path: String): Bitmap? {
    return try {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val data = mmr.embeddedPicture
        data?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    } catch (e: Exception) {
        null
    }
}

private fun loadMusics(
    context: Context,
    uri: Uri,
    cb: (HashSet<Album>, HashSet<Artist>) -> Unit,
    mainDatastore: MainDataStore
): Flow<Music> = flow {
    val projection = arrayOf(
        MediaInfo.ALBUM,
        MediaInfo.ALBUM_ID,
        MediaInfo.ARTIST,
        MediaInfo.ARTIST_ID,
        MediaInfo.DATA,
        MediaInfo.DISPLAY_NAME,
        MediaInfo.DURATION
    ) // or null to be easier
    val selection = MediaInfo.IS_MUSIC + " != 0"
    val sortOrder = MediaInfo.DISPLAY_NAME + " ASC"
    val cursor: Cursor = context.contentResolver
        .query(uri, projection, selection, null, sortOrder) ?: return@flow

    if (cursor.moveToFirst()) {

        val albums = hashMapOf<String, Album>() //albumDataSource.getItems().first().toMap { it.name }
        val artists = hashMapOf<String, Artist>() //artistDataSource.getItems().first().toMap { it.name }

        val setArtist = hashSetOf<Artist>()
        val setAlbum = hashSetOf<Album>()

        val nameColumn = cursor.getColumnIndex(MediaInfo.DISPLAY_NAME)
        val dataColumn = cursor.getColumnIndex(MediaInfo.DATA)
        val albumColumn = cursor.getColumnIndex(MediaInfo.ALBUM)
        val artistColumn = cursor.getColumnIndex(MediaInfo.ARTIST)
        val albumIdColumn = cursor.getColumnIndex(MediaInfo.ALBUM_ID)
        val artistIdColumn = cursor.getColumnIndex(MediaInfo.ARTIST_ID)
        val durationColumn = cursor.getColumnIndex(MediaInfo.DURATION)

        var songName: String
        var path: String
        var albumName: String
        var artistName: String
        var albumId: Long
        var artistId: Long
        var time: Int
        do {
            songName = cursor.getString(nameColumn)

            path = cursor.getString(dataColumn)

            albumName = cursor.getString(albumColumn)

            artistName = cursor.getString(artistColumn)

            albumId = albums[albumName]?.id ?: cursor.getLong(albumIdColumn).also {
                setAlbum.add(Album(it, albumName))
            }

            artistId = artists[artistName]?.id ?: cursor.getLong(artistIdColumn).also {
                setArtist.add(Artist(it, artistName))
            }

            time = cursor.getInt(durationColumn)

            emit(
                Music(
                    name = songName,
                    time = time,
                    data = path,
                    artistId = artistId,
                    albumId = albumId
                )/*.also {
                        musicDataSource.insertItems(it)
                    }*/
            )

        } while (cursor.moveToNext())

/*            albumDataSource.insertItems(*setAlbum.toTypedArray())
            artistDataSource.insertItems(*setArtist.toTypedArray())*/
        cb(setAlbum, setArtist)
    }

    mainDatastore.updateHasBeenLoaded(true)
    cursor.close()
}.flowOn(Dispatchers.IO)

fun loadMusics(
    context: Context,
    mainDatastore: MainDataStore,
    cb: (HashSet<Album>, HashSet<Artist>) -> Unit,
): Flow<List<Music>> {
    val uri = MediaInfo.EXTERNAL_CONTENT_URI
    return loadMusics(context, uri, cb, mainDatastore).collectAsList(20)
}

