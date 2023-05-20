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

    private var map = hashMapOf<String, Song>()

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
                _currentSong.value = event.song
                val mediaItem = MediaItem.fromUri(event.song.songURL)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()

                if (!_isPausePlayClicked.value) _isPausePlayClicked.value =
                    !_isPausePlayClicked.value

                //everytime we need to refresh the playlist whenever user clicks a new song
                viewModelScope.launch {
                    setupAllSongsForPlaying(songsList)
                }
            }
            SongsListEvents.onPlayerClick -> {
                TODO()
            }
            SongsListEvents.onplayNextSong -> {
                player.seekToNextMediaItem()
                updatePlayerUI()
            }
            SongsListEvents.onplayPreviousSong -> {
                player.seekToPreviousMediaItem()
                updatePlayerUI()
            }
        }
    }

    fun updatePlayerUI() {
        val index = player.currentMediaItem.toString()
        _currentSong.value = map.get(index)!!
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
            //make a hashmap to fetch current song from playlist
            map.put(mediaItem.toString(), song)
        }
        player.apply {
            prepare()
            Log.d(LOG, "All songs are loaded into the player")
        }
    }

    inner class PlayerListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                //The player finished playing all media.
                Player.STATE_ENDED -> {
                    Log.d(LOG, "Player: State Ended")
                    this@SongsListViewModel.onSongsListEvent(SongsListEvents.onplayNextSong)
                }
                Player.STATE_BUFFERING -> Unit
                Player.STATE_IDLE -> Unit
                Player.STATE_READY -> Unit
            }
        }
    }

}