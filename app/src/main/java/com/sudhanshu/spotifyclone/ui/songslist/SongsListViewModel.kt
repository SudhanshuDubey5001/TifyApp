package com.sudhanshu.spotifyclone.ui.songslist

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sudhanshu.spotifyclone.Exoplayer.ExoplayerJobs
import com.sudhanshu.spotifyclone.Exoplayer.ExoplayerService
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants
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
    @ApplicationContext context: Context
) : ViewModel() {

    private var _songsList = MutableStateFlow(listOf<Song>())
    val songsListFlow = _songsList.asStateFlow()

    private var _currentSong = MutableStateFlow(Song())
    val currentSongFlow = _currentSong.asStateFlow()

    private var _isPausePlayClicked = MutableStateFlow(false)
    val isPausePlayClicked = _isPausePlayClicked.asStateFlow()

    private lateinit var songsList: List<Song>

    private var controller: ListenableFuture<MediaController>

    private val exoplayerInstance: ExoplayerJobs = ExoplayerJobs(player, _currentSong)
    private lateinit var mediaController: MediaController
//    private lateinit var player: Player

    init {
        player.addListener(exoplayerInstance)
        getSongsCollection()
        val sessionToken =
            SessionToken(context, ComponentName(context, ExoplayerService::class.java))
        controller = MediaController.Builder(context, sessionToken).buildAsync()
        controller.addListener({
            mediaController = controller.get()
            mediaController.addListener(object : Player.Listener {
                //to sync with inside app player media play/pause button
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    _isPausePlayClicked.value = isPlaying
                }
            })
        }, MoreExecutors.directExecutor())
    }

    override fun onCleared() {
        exoplayerInstance.performReleaseInstances() //just in case if Hilt is unable to for some unknown reasons
        MediaController.releaseFuture(controller)
        Log.d(LOG, "onCleared called!!")
        super.onCleared()
    }

    //possible events --->
    fun onPlayerEvents(event: PlayerEvents) {
        when (event) {
            PlayerEvents.onPausePlay -> {
                _isPausePlayClicked.value = !_isPausePlayClicked.value
                exoplayerInstance.performPausePlay(_isPausePlayClicked.value)
            }
            is PlayerEvents.onPlayNewSong -> {
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
                exoplayerInstance.performPlayNextSong()
            }
            PlayerEvents.onplayPreviousSong -> {
                exoplayerInstance.performPlayPreviousSong()
            }
        }
    }

    //now we will make a network call to get all the songs in the list
    fun getSongsCollection() {
        songCollection.get().addOnSuccessListener { documents ->
            if (documents != null) {
                viewModelScope.launch {
                    songsList = documents.toObjects(Song::class.java)
                    _songsList.emit(songsList)
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