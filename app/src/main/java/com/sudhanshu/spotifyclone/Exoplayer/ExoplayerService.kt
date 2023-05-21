package com.sudhanshu.spotifyclone.Exoplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sudhanshu.spotifyclone.MainActivity
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.other.Constants
import com.sudhanshu.spotifyclone.other.Constants.LOG
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class ExoplayerService
    : MediaSessionService() {

    @Inject
    lateinit var mediaSession: MediaSession

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onCreate() {
        super.onCreate()

        //here we are connecting the mediacontroller (mediasession.player) to the given session so that
        //other outside apps who wants to control the music can do so
        Log.d(LOG, "onCreate service Called!!")
        val sessionToken = SessionToken(this, ComponentName(this, this::class.java))
        val controller = MediaController.Builder(this, sessionToken).buildAsync()
        controller.addListener(
            { mediaSession.player = controller.get() },
            MoreExecutors.directExecutor()
        )
    }

    override fun onUpdateNotification(session: MediaSession) {
        super.onUpdateNotification(session)
        Log.d(LOG, "Song: "+ mediaSession.player.currentMediaItem.toString())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSession.player.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun createNotification(): Notification {
        return Notification.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Only with you")
            .setContentText("Alan Watts")
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
//            .setContentIntent(pendingIntent)
            .setTicker("Ticker text")
            .build()
    }

    override fun onDestroy() {
        Log.d(LOG, "Service onDestry called")
        mediaSession.player.release()
        stopSelf()
        super.onDestroy()
    }

//    The MediaSession receives commands from the controller through its SessionCallback.
    //Initializing a MediaSession creates a default implementation of SessionCallback that automatically
    // handles all commands that your MediaController sends to your player.
}