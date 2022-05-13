package com.example.musicplayer.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.musicplayer.data.local.data_store.main.MainDataStore
import com.example.musicplayer.data.repository.MusicLocalRepository
import com.example.musicplayer.utils.loadMusics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LoadMusicWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val localRepository: MusicLocalRepository,
    private val mainDataStore: MainDataStore
) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            with(localRepository) {
                val flow = loadMusics(context, mainDataStore) { albums, artists ->
                    loadAlbums(albums)
                    loadArtists(artists)
                }
                loadMusics(flow)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}