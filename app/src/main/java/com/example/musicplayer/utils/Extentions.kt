package com.example.musicplayer.utils

import android.content.Context
import android.content.SharedPreferences
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*


typealias MediaInfo = MediaStore.Audio.Media

fun Int.timeFormatter(): String {
    val hour = this / (60 * 60)
    val min = this / 60
    val sec = this % 60
    return if (hour == 0) {
        "$min:${stringFormatter(sec)}"
    } else {
        "$hour:${stringFormatter(min)}"
    }
}

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

fun <T, K> List<T>.toMap(key: T.() -> K): HashMap<K, T> {
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

fun Fragment.repeateLaunchOnState(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state, block)
    }
}