package com.sudhanshu.spotifyclone.Exoplayer

import android.content.Intent
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants

class ExoplayerJobs(
    private val player: Player
) : Player.Listener{

    val mapSongMediaItem = hashMapOf<String, Song>()

    fun performPausePlay(value: Boolean) {
        player.playWhenReady = value
    }

    fun performPlayNewSong(song: Song) {
        val mediaItem = MediaItem.fromUri(song.songURL)
        player.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun performPreparePlaylist(songList: List<Song>){
        for (song in songList) {
            val mediaItem = MediaItem.fromUri(song.songURL).apply {
            }
            player.playlistMetadata = MediaMetadata.Builder().setTitle(song.title).build()
            player.addMediaItem(mediaItem)
            //make a hashmap to fetch current song from playlist
            mapSongMediaItem.put(mediaItem.toString(), song)
        }
        player.prepare()
        Log.d(Constants.LOG, "All songs are loaded into the player")
    }

    fun performPlayNextSong(): Song{
        player.seekToNextMediaItem()
        Log.d(Constants.LOG, "Next song initiated")
        return mapSongMediaItem.get(player.currentMediaItem.toString())!!
    }

    fun performPlayPreviousSong(): Song{
        player.seekToPreviousMediaItem()
        Log.d(Constants.LOG, "Previous song initiated")
        return mapSongMediaItem.get(player.currentMediaItem.toString())!!
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            //The player finished playing all media.
            Player.STATE_ENDED -> {
                Log.d(Constants.LOG, "Player: State Ended")
                performPlayNextSong()
            }
            Player.STATE_BUFFERING -> Unit
            Player.STATE_IDLE -> Unit
            Player.STATE_READY -> Unit
        }
    }
}