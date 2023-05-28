package com.sudhanshu.spotifyclone.Exoplayer


import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants
import com.sudhanshu.spotifyclone.other.Constants.LOG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ExoplayerJobs(
    private val player: Player,
    private var currentSong: MutableStateFlow<Song>,
    private var currentMediaPosition: MutableStateFlow<Float>,
    private var currentMediaDurationInMinutes: MutableStateFlow<String>,
    private var currentMediaProgressInMinutes: MutableStateFlow<String>,
    private var isPausePlayClicked: MutableStateFlow<Boolean>,
    private var isPlayerBuffering: MutableStateFlow<Boolean>,
    private var isShuffleClicked: MutableStateFlow<Boolean>,
    private val viewModelScope: CoroutineScope
) : Player.Listener {

    val mapSongMediaItem = hashMapOf<String, Song>()
    var duration: Long = 0
    lateinit var controller: ListenableFuture<MediaController>
//    var isShuffleClick = false
    var isRepeatClick = false

    fun performPausePlay() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun shuffleClick() {
        isShuffleClicked.value = !isShuffleClicked.value
        player.shuffleModeEnabled = isShuffleClicked.value
    }

    fun repeatClick() {
        isRepeatClick = !isRepeatClick
        if (isRepeatClick) player.repeatMode = Player.REPEAT_MODE_ONE
        else player.repeatMode = Player.REPEAT_MODE_OFF
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
        currentSong.value.isBackgroundColorEnabled = false
        currentSong.value = song
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        //reset position
        currentMediaPosition.value = 0f

        val song = mapSongMediaItem.get(mediaItem.toString())
        Log.d(LOG, "Media item changed: " + song)
        if (song != null) {
            currentSong.value.isBackgroundColorEnabled = false
            currentSong.value = song
            currentSong.value.isBackgroundColorEnabled = true
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        isPausePlayClicked.value = isPlaying
    }

    fun performPreparePlaylist(songList: List<Song>) {
        isPlayerBuffering.value = true
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
        isPlayerBuffering.value = false
        Log.d(LOG, "All songs are loaded into the player")
    }

    fun performPlayNextSong() {
        player.seekToNextMediaItem()
        Log.d(Constants.LOG, "Next song initiated")
    }

    fun performPlayPreviousSong() {
        player.seekToPreviousMediaItem()
        Log.d(Constants.LOG, "Previous song initiated")
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        Log.d(LOG, "OnPlaybackStatechanged called!!")
        when (playbackState) {
            //The player finished playing all media.
            Player.STATE_ENDED -> {
                Log.d(LOG, "Player: State Ended")
                if (player.hasNextMediaItem()) {
                    if(player.hasNextMediaItem()) performPlayNextSong()
                }
            }
            Player.STATE_BUFFERING -> {
                isPlayerBuffering.value = true
                currentMediaProgressInMinutes.value = "00:00"
                currentMediaDurationInMinutes.value = "00:00"
                Log.d(LOG, "STATE BUFFERING")
            }
            Player.STATE_IDLE -> {
                Log.d(LOG, "STATE IDLE")
            }
            Player.STATE_READY -> {
                isPlayerBuffering.value = false
                Log.d(LOG, "STATE READY")
            }
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

    fun setupMediaNotification(context: Context) {
        val sessionToken =
            SessionToken(context, ComponentName(context, ExoplayerService::class.java))
        controller = MediaController.Builder(context, sessionToken).buildAsync()
        controller.addListener({
            val mediaController = controller.get()
            mediaController.addListener(object : Player.Listener {
                //to sync with inside app player media play/pause button
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    isPausePlayClicked.value = isPlaying
                    duration = mediaController.duration
                    if (duration == -9223372036854775807) duration =
                        0   //buffering is throwing back this number
                    currentMediaDurationInMinutes.value = convertDurationLongToTime(duration)
                    viewModelScope.launch {
                        while (isPausePlayClicked.value) {
                            updatePlayerSeekProgress(player.currentPosition)
                            delay(1000)
                        }
                    }
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                    when (reason) {
                        Player.DISCONTINUITY_REASON_SEEK -> {
                            updatePlayerSeekProgress(newPosition.contentPositionMs)
                            player.seekTo(newPosition.contentPositionMs)
                        }
                        Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> Unit
                        Player.DISCONTINUITY_REASON_INTERNAL -> Unit
                        Player.DISCONTINUITY_REASON_REMOVE -> Unit
                        Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT -> Unit
                        Player.DISCONTINUITY_REASON_SKIP -> Unit
                    }
                }
            })
        }, MoreExecutors.directExecutor())
    }

    fun onSeekMusicDone(value: Float) {
        Log.d(LOG, "onSeekMusicDone value received = $value")
        val longValue = (value * duration).toLong()
        Log.d(LOG, "Seek to long value: $longValue")
        player.seekTo(longValue)
    }

    fun updatePlayerSeekProgress(pos: Long) {
        currentMediaProgressInMinutes.value = convertDurationLongToTime(pos)
        val progress = pos.toFloat() / duration.toFloat()
        if (!progress.isNaN()) currentMediaPosition.value = progress
        Log.d(LOG, "Current Progress = " + currentMediaPosition.value)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Log.d(LOG, "Player error: ${error}")
    }

    fun performReleaseInstances() {
        player.release()
        MediaController.releaseFuture(controller)
    }

    fun convertDurationLongToTime(getDurationInMillis: Long): String {
        val convertMinutes = String.format(
            "%02d", TimeUnit.MILLISECONDS.toMinutes(getDurationInMillis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(getDurationInMillis))
        ) //I needed to add this part.
        val convertSeconds = String.format(
            "%02d", TimeUnit.MILLISECONDS.toSeconds(getDurationInMillis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDurationInMillis))
        )
        return "$convertMinutes:$convertSeconds"
    }
}