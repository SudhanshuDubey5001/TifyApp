package com.sudhanshu.spotifyclone.Exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerNotificationManager.NotificationListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.other.Constants.NOTIFICATION_CHANNEL_ID
import com.sudhanshu.spotifyclone.other.Constants.NOTIFICATION_ID

class MusicNotificationManager(
    private val context: Context,
    sessionToken: SessionToken,
    notificationListener: NotificationListener
) {
//    private val notificationManager: PlayerNotificationManager
//
//    init {
//        val mediaController = MediaController.Builder(context,sessionToken)
//
//        notificationManager = PlayerNotificationManager.Builder(
//            context,
//            NOTIFICATION_ID,
//            NOTIFICATION_CHANNEL_ID
//        ).setChannelNameResourceId(R.string.notification_channel_name)
//            .setChannelDescriptionResourceId(R.string.notification_channel_description)
////            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
//            .setNotificationListener(notificationListener)
//            .build()
//            .apply {
//                setSmallIcon(androidx.media3.session.R.drawable.media_session_service_notification_ic_music_note)
////                setMediaSessionToken(sessionToken)
//            }
//    }
//
//
//    //a adapter class for assigning data relevant to notification
//    private inner class DescriptionAdapter(
//        private val mediaController: MediaController
//    ): PlayerNotificationManager.MediaDescriptionAdapter{
//        override fun getCurrentContentTitle(player: Player): CharSequence {
//            return mediaController.mediaMetadata.title.toString()
//        }
//
//        override fun createCurrentContentIntent(player: Player): PendingIntent? {
//            return mediaController.sessionActivity
//        }
//
//        override fun getCurrentContentText(player: Player): CharSequence? {
//            return mediaController.mediaMetadata.subtitle
//        }
//
//        override fun getCurrentLargeIcon(
//            player: Player,
//            callback: PlayerNotificationManager.BitmapCallback
//        ): Bitmap? {
//            Glide.with(context).asBitmap()
//                .load(mediaController.mediaMetadata.extras?.getString(MediaMetadata.METADATA_KEY_MEDIA_URI))
//                .into(object : CustomTarget<Bitmap>(){
//                    override fun onResourceReady(
//                        resource: Bitmap,
//                        transition: Transition<in Bitmap>?
//                    ) {
//                        callback.onBitmap(resource)
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) = Unit
//
//                })
//            return null
//        }
//    }
//
//
//    //in order to show notification
//    fun showNotification(player: Player){
//        notificationManager.setPlayer(player)
//    }
}