package com.sudhanshu.spotifyclone.Exoplayer.callbacks

import android.app.Notification
import android.app.Service
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.media3.ui.PlayerNotificationManager
import com.sudhanshu.spotifyclone.Exoplayer.MusicService
import com.sudhanshu.spotifyclone.other.Constants.NOTIFICATION_ID

class MusicPlayerNotificationListener(
    private val musicService: MusicService
) : PlayerNotificationManager.NotificationListener {

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        //stop the service when the user removes the notification by swiping away
        musicService.apply {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
            stopSelf()
            isForegroundService = false
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        //start the service here
        musicService.apply {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                //now we want to start the foreground service using Service class
                startForeground(NOTIFICATION_ID, notification)
                isForegroundService = true
            }
        }
    }
}