package com.sudhanshu.spotifyclone.ui.songslist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.google.firebase.firestore.CollectionReference
import com.sudhanshu.spotifyclone.Exoplayer.callbacks.PlayerListener
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//keeping this outside viewModel otherwise Compose is not detecting changes to this list
private var _songsList = MutableStateFlow(listOf<Song>())
val songsListFlow = _songsList.asStateFlow()

@HiltViewModel
class SongsListViewModel @Inject constructor(
    private val player: ExoPlayer,
    private val songCollection: CollectionReference
) : ViewModel() {

    var pauseANDplayTrack: Boolean = false

    init {
        //get the songs list
        getSongsCollection()
        val listener = PlayerListener(player)
        player.addListener(listener)
    }

    //possible events --->
    fun onSongsListEvent(event: SongsListEvent) {
        when (event) {
            SongsListEvent.onPausePlay -> {
                pauseANDplayTrack = !pauseANDplayTrack
                player.playWhenReady = !pauseANDplayTrack
            }
            is SongsListEvent.onPlayNewSong -> {
                viewModelScope.launch {
                    val mediaItem = MediaItem.fromUri(event.song.songURL)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                }
            }
            SongsListEvent.onPlayerClick -> {
                TODO()
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
                    setupAllSongsForPlaying(songs)
                }
//                to check if we are getting the songs ----->
//                for (document in documents) {
//                    val song = document.toObject(Song::class.java)
//                    //check if you are getting all the songs
//                    Log.d(Constants.LOG, "title: " + song.title)
//                }
            }
        }.addOnFailureListener { exception ->
            Log.d(Constants.LOG, exception.toString())
        }
    }

    //for playing all songs one after other
    suspend fun setupAllSongsForPlaying(songs: List<Song>) {
        for (song in songs) {
            val mediaItem = MediaItem
                .fromUri(song.songURL)
            player.addMediaItem(mediaItem)
        }
        player.apply {
            playWhenReady = false   //we don't to play song as soon as the song is ready
            prepare()
        }
    }
}