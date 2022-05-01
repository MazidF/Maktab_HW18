package com.example.musicplayer.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

sealed class Result<T>(
    val data: T?,
    val error: Throwable?,
) {
    open class Success<T>(data: T) : Result<T>(data, null)
    class Error<T>(error: Throwable) : Result<T>(null, error)
    class Loading<T>(data: T? = null) : Result<T>(data, null)

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> error(error: Throwable): Result<T> = Error(error)
        fun <T> loading(data: T? = null): Result<T> = Loading(data)
    }
}