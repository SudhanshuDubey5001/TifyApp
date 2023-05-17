package com.sudhanshu.spotifyclone.Exoplayer

import android.app.PendingIntent
import android.media.MediaSession2
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.service.media.MediaBrowserService
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

const val SERVICE_TAG = "service tag"

@AndroidEntryPoint  //use this if you want to use DI
class MusicService : MediaBrowserService(){

    @Inject
    lateinit var datasource: DefaultDataSource.Factory

    @Inject
    lateinit var exoplayer: ExoPlayer

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession : MediaSession

    override fun onCreate() {
        super.onCreate()
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        mediaSession = MediaSession.Builder(this, exoplayer)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        //release the mediaSession instance
        mediaSession?.run {
            player.release()
            release()
        }
        //cancel the music service when app is permanently closed
        serviceScope.cancel()
    }

    override fun onGetRoot(p0: String, p1: Int, p2: Bundle?): BrowserRoot? {

    }

    override fun onLoadChildren(p0: String, p1: Result<MutableList<MediaBrowser.MediaItem>>) {
        TODO("Not yet implemented")
    }
}