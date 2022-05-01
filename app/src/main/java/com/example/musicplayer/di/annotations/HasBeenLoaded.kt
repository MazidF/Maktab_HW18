package com.example.musicplayer.di.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HasBeenLoaded

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherMain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherIO

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherDefault