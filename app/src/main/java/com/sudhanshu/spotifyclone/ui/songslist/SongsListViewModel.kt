package com.sudhanshu.spotifyclone.ui.songslist

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.firestore.CollectionReference
import com.sudhanshu.spotifyclone.Exoplayer.ExoplayerJobs
import com.sudhanshu.spotifyclone.Exoplayer.ExoplayerService
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants.LOG
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsListViewModel @Inject constructor(
    private val player: Player,
    private val songCollection: CollectionReference,
) : ViewModel() {

    private var _songsList = MutableStateFlow(listOf<Song>())
    val songsListFlow = _songsList.asStateFlow()

    private var _currentSong = MutableStateFlow(Song())
    val currentSongFlow = _currentSong.asStateFlow()

    private var _isPausePlayClicked = MutableStateFlow(false)
    val isPausePlayClicked = _isPausePlayClicked.asStateFlow()

    private lateinit var songsList: List<Song>

    private var exoplayerInstance: ExoplayerJobs

    init {
        //get the songs list
        getSongsCollection()
        exoplayerInstance = ExoplayerJobs(player)
    }

    //possible events --->
    fun onPlayerEvents(event: PlayerEvents) {
        when (event) {
            PlayerEvents.onPausePlay -> {
                _isPausePlayClicked.value = !_isPausePlayClicked.value
                exoplayerInstance.performPausePlay(_isPausePlayClicked.value)
            }
            is PlayerEvents.onPlayNewSong -> {
                _currentSong.value = event.song
                exoplayerInstance.performPlayNewSong(event.song)

                if (!_isPausePlayClicked.value) _isPausePlayClicked.value =
                    !_isPausePlayClicked.value

                //everytime we need to refresh the playlist whenever user clicks a new song
                viewModelScope.launch {
                    exoplayerInstance.performPreparePlaylist(songsList)
                }
            }
            PlayerEvents.onPlayerClick -> {
                TODO()
            }
            PlayerEvents.onplayNextSong -> {
                _currentSong.value = exoplayerInstance.performPlayNextSong()
            }
            PlayerEvents.onplayPreviousSong -> {
                _currentSong.value = exoplayerInstance.performPlayPreviousSong()
            }
        }
    }

    //now we will make a network call to get all the songs in the list
    fun getSongsCollection() {
        songCollection.get().addOnSuccessListener { documents ->
            if (documents != null) {
                viewModelScope.launch {
                    val songs = documents.toObjects(Song::class.java)
                    _songsList.emit(songs)
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
}