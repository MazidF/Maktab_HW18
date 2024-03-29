package com.example.musicplayer.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.flow.*

// useless
class LiveDataWrapper<T>(t: T? = null) {
    private val liveData by lazy {
        if (t == null) {
            MutableLiveData<T>()
        } else {
            MutableLiveData(t)
        }
    }

    fun value() = liveData.value

    fun valueNotNull() = value()!!

    fun postValue(t: T?) {
        liveData.postValue(t)
    }

    fun apply(apply: (T) -> Unit) {
        postValue(value()?.apply(apply))
    }

    fun setValue(t: T?) {
        liveData.value = t
    }

    fun liveData(): LiveData<T> = liveData

    fun <R> map(transformer: (T) -> R): LiveData<R> {
        return Transformations.map(liveData, transformer)
    }
}

// useless
class StateFlowWrapper<T>(t: T) {
    private val stateFlow by lazy {
        MutableStateFlow(t)
    }

    fun value() = stateFlow.value

    suspend fun emit(t: T) {
        stateFlow.emit(t)
    }

    suspend fun collect(collector: FlowCollector<T>) {
        stateFlow.collect(collector)
    }

    suspend fun first() = stateFlow.first()
}

data class Triple <A, B, C>(
    val first: A,
    val second: B,
    val third: C
)