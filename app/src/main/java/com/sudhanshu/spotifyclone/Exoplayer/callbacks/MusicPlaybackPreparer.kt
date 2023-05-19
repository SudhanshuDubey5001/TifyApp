package com.sudhanshu.spotifyclone.Exoplayer.callbacks

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.ListenableFuture

class MusicPlaybackPreparer : MediaSession.Callback{

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        return super.onAddMediaItems(mediaSession, controller, mediaItems)
    }
}