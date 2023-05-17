package com.sudhanshu.spotifyclone.Exoplayer


import android.media.MediaDescription
import android.os.Bundle
import com.sudhanshu.spotifyclone.data.remote.SongsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_MUSIC
import android.media.MediaMetadata.*
import android.media.browse.MediaBrowser
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource


//in this class we already have a list of songs that we got from firebase
class FirebaseMusicSource @Inject constructor(
    private val songsDatabase: SongsDatabase
) {

    var songs = emptyList<MediaMetadata>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = State.STATE_INITIALIZING
        val allSongs = songsDatabase.getSongCollection()
        songs = allSongs.map { song ->
            val bundle = Bundle()
            bundle.putString(METADATA_KEY_MEDIA_URI, song.songURL)
            bundle.putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageURL)
            bundle.putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageURL)
            bundle.putString(METADATA_KEY_ALBUM_ART_URI, song.imageURL)
            bundle.putString(METADATA_KEY_MEDIA_ID, song.mediaID)

            MediaMetadata.Builder()
                .setTitle(song.title)
                .setDisplayTitle(song.title)
                .setArtist(song.subtitle)
                .setMediaType(MEDIA_TYPE_MUSIC)
                .setDescription(song.subtitle)
                .setArtworkUri(song.imageURL.toUri())
                .setExtras(bundle)  //all the extra info
                .build()
        }
        state = State.STATE_INITIALIZED     //we done with loading songs metadata
    }

    //now we will concatenate all the media sources as we want to play songs one after the other
    fun concatenateMediaSources(dataSourceFactory: DataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.fromUri(
                        song.extras?.getString(METADATA_KEY_MEDIA_URI).toString()
                    )
                )
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

//    now if we create more albums or song list, we need some kind of file manager setting, so that we can browse
//  through different albums.

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescription.Builder()
            .setMediaUri(song.extras?.getString(METADATA_KEY_MEDIA_URI)?.toUri())
            .setTitle(song.title)
            .setSubtitle(song.subtitle)
            .setMediaId(song.extras?.getString(METADATA_KEY_MEDIA_ID).toString())
            .setIconUri(song.artworkUri)
            .build()
        MediaBrowser.MediaItem(desc, MediaBrowser.MediaItem.FLAG_PLAYABLE)
    }

    //services cannot wait for getting a ready source as they need it immediately otherwise they throw an error
    //so we made a state var here that can store the state of the songs (it needs some time to load)
    // and once they are ready or "initialized", we can perform an action (in whenReady)
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = State.STATE_CREATED
        set(value) {
            if (value == State.STATE_INITIALIZED || value == State.STATE_ERROR) {
                //we don't want any other thread to interfere with this
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == State.STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if (state == State.STATE_CREATED || state == State.STATE_INITIALIZING) {
            onReadyListeners.add(action)
            return false
        } else {
            action(state == State.STATE_INITIALIZED)
            return true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}