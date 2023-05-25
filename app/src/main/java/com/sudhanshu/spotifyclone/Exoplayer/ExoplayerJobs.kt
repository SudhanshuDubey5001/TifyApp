package com.sudhanshu.spotifyclone.Exoplayer


import android.media.MediaMetadata.*
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants
import com.sudhanshu.spotifyclone.other.Constants.LOG
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ExoplayerJobs(
    private val player: Player,
    private var currentSong: MutableStateFlow<Song>
) : Player.Listener {

    val mapSongMediaItem = hashMapOf<String, Song>()

    fun performPausePlay(value: Boolean) {
        player.playWhenReady = value
    }

    fun performPlayNewSong(song: Song) {
        val metadata = getMetaDataFromSong(song)
        val mediaItem = MediaItem.Builder()
            .setUri(song.songURL)
            .setMediaId(song.mediaID)
            .setMediaMetadata(metadata)
            .build()

        player.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        currentSong.value = song
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        val song = mapSongMediaItem.get(mediaItem.toString())
        Log.d(LOG, "Media item changed: " + song)
        if (song != null) currentSong.value = song
    }

    fun performPreparePlaylist(songList: List<Song>) {
        for (song in songList) {
            val metadata = getMetaDataFromSong(song)
            val mediaItem = MediaItem.Builder().apply {
                setUri(song.songURL)
                setMediaId(song.mediaID)
                setMediaMetadata(metadata)
            }.build()
            player.addMediaItem(mediaItem)
            //make a hashmap to fetch current song from playlist
            mapSongMediaItem.put(mediaItem.toString(), song)
        }
        player.prepare()
        Log.d(Constants.LOG, "All songs are loaded into the player")
    }

    fun performPlayNextSong() {
        if (player.hasNextMediaItem()) player.seekToNextMediaItem()
        Log.d(Constants.LOG, "Next song initiated")
    }

    fun performPlayPreviousSong() {
        if (player.hasPreviousMediaItem()) player.seekToPreviousMediaItem()
        Log.d(Constants.LOG, "Previous song initiated")
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        Log.d(LOG, "OnPlaybackStatechanged called!!")
        when (playbackState) {
            //The player finished playing all media.
            Player.STATE_ENDED -> {
                Log.d(LOG, "Player: State Ended")
                if (player.hasNextMediaItem()) performPlayNextSong()
            }
            Player.STATE_BUFFERING -> Unit
            Player.STATE_IDLE -> Unit
            Player.STATE_READY -> Unit
        }
    }

    fun getMetaDataFromSong(song: Song): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle(song.title)
            .setAlbumTitle(song.title)
            .setDisplayTitle(song.title)
            .setArtist(song.subtitle)
            .setAlbumArtist(song.subtitle)
            .setArtworkUri(song.imageURL.toUri())
            .build()
    }

    fun performReleaseInstances() {
        player.release()
    }
}