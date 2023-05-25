package com.sudhanshu.spotifyclone.Exoplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sudhanshu.spotifyclone.MainActivity
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.other.Constants
import com.sudhanshu.spotifyclone.other.Constants.LOG
import com.sudhanshu.spotifyclone.other.Constants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executor
import java.util.concurrent.Future
import javax.inject.Inject

@AndroidEntryPoint
class ExoplayerService
    : MediaSessionService(){

    @Inject
    lateinit var mediaSession: MediaSession

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG, "onCreate service Called!!")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSession.player.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        Log.d(LOG, "Service onDestry called")
        stopSelf()
        super.onDestroy()
    }
}