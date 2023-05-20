package com.sudhanshu.spotifyclone.ui.songslist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.firebase.firestore.CollectionReference
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants.LOG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SongsListViewModel @Inject constructor(
    private val player: ExoPlayer,
    private val songCollection: CollectionReference
) : ViewModel() {

    private var _songsList = MutableStateFlow(listOf<Song>())
    val songsListFlow = _songsList.asStateFlow()

    private var _currentSong = MutableStateFlow(Song())
    val currentSongFlow = _currentSong.asStateFlow()

    private var _isPausePlayClicked = MutableStateFlow(false)
    val isPausePlayClicked = _isPausePlayClicked.asStateFlow()

    private lateinit var songsList: List<Song>
    private val listener = PlayerListener()

    private var isShuffleON = true

    init {
        //get the songs list
        getSongsCollection()
    }

    //possible events --->
    fun onSongsListEvent(event: SongsListEvents) {
        when (event) {
            SongsListEvents.onPausePlay -> {
                _isPausePlayClicked.value = !_isPausePlayClicked.value
                player.playWhenReady = _isPausePlayClicked.value
            }
            is SongsListEvents.onPlayNewSong -> {
                viewModelScope.launch {
                    _currentSong.value = event.song
                    val mediaItem = MediaItem.fromUri(event.song.songURL)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()

                    if (!_isPausePlayClicked.value) _isPausePlayClicked.value =
                        !_isPausePlayClicked.value

//                    setupAllSongsForPlaying(songsList)
                }
            }
            SongsListEvents.onPlayerClick -> {
                TODO()
            }
            is SongsListEvents.updateCurrentSong -> {
                _currentSong.value = event.song
            }
            SongsListEvents.playNextSong -> {
                if (isShuffleON) listener.prepareRandomSongFromList_andPlayNOW()
                else listener.prepareSequentialSongFromList()
                player.seekToNextMediaItem()
            }
            SongsListEvents.playPreviousSong -> player.seekToPreviousMediaItem()
        }
    }


    //now we will make a network call to get all the songs in the list
    fun getSongsCollection() {
        songCollection.get().addOnSuccessListener { documents ->
            if (documents != null) {
                viewModelScope.launch {
                    val songs = documents.toObjects(Song::class.java)
                    _songsList.emit(songs)
                    player.addListener(PlayerListener())
                    songsList = songs.toList()
                }
//                to check if we are getting the songs ----->
//                for (document in documents) {
//                    val song = document.toObject(Song::class.java)
//                    //check if you are getting all the songs
//                    Log.d(Constants.LOG, "title: " + song.title)
//                }
            }
        }.addOnFailureListener { exception ->
            Log.d(LOG, exception.toString())
        }
    }

    //for playing all songs one after other
    suspend fun setupAllSongsForPlaying(songs: List<Song>) {
        for (song in songs) {
            val mediaItem = MediaItem
                .fromUri(song.songURL)
            player.addMediaItem(mediaItem)
            Log.d(LOG, "Song : " + song.title)
        }
        player.apply {
            prepare()
            Log.d(LOG, "All songs are loaded into the player")
        }
    }

    inner class PlayerListener : Player.Listener {
        private var index = 0
        private lateinit var nextSongInstance: Song
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                //This is the initial state, the state when the player is stopped, and when playback failed.
                // The player will hold only limited resources in this state.
                Player.STATE_IDLE -> {
                    Log.d(LOG, "Player: State Idle")
                }
                //The player is not able to immediately play from its current position.
                // This mostly happens because more data needs to be loaded.
                Player.STATE_BUFFERING -> {
                    Log.d(LOG, "Player: State buffering")
                }
                //The player is able to immediately play from its current position.
                Player.STATE_READY -> {
                    Log.d(LOG, "Player: State ready")
                }
                //The player finished playing all media.
                Player.STATE_ENDED -> {
                    Log.d(LOG, "Player: State Ended")
                    prepareRandomSongFromList_andPlayNOW()
                }
            }
        }

        fun prepareRandomSongFromList_andPlayNOW() {
            val randomInteger = Random.nextInt(0, songsList.size)
            nextSongInstance = songsList[randomInteger]
            val mediaItem = MediaItem.fromUri(nextSongInstance.songURL)
            playNowFromPlaylist(mediaItem)
        }

        fun prepareSequentialSongFromList() {
            if (index + 1 < songsList.size) index++
            else index = 0
            nextSongInstance = songsList[index++]
            val mediaItem = MediaItem.fromUri(nextSongInstance.songURL)
            playNowFromPlaylist(mediaItem)
        }

        fun playNowFromPlaylist(mediaItem: MediaItem) {
            player.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
            _currentSong.value = nextSongInstance
        }

        fun addSongInPlaylist(mediaItem: MediaItem) {
            player.apply {
                addMediaItem(mediaItem)
                prepare()
            }
        }
    }

}