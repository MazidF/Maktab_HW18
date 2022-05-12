package com.example.musicplayer.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        setValue(value()?.apply(apply))
    }

    fun setValue(t: T?) {
        liveData.value = t
    }

    fun liveData(): LiveData<T> = liveData

    fun <R> map(transformer: (T) -> R): LiveData<R> {
        return Transformations.map(liveData, transformer)
    }
}

// TODO: change this useless class
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
}

data class Triple <A, B, C>(
    val first: A,
    val second: B,
    val third: C
)