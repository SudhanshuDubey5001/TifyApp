package com.sudhanshu.spotifyclone.ui.songslist

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//keeping this outside viewModel otherwise Compose is not detecting changes to this list
private var _songsList = MutableStateFlow(listOf<Song>())
val songsListFlow = _songsList.asStateFlow()

@HiltViewModel
class SongsListViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val firestore = Firebase.firestore
    private val songCollection = firestore.collection(Constants.SONG_COLLECTION)
    private val player = ExoPlayer.Builder(context).build()

    //possible events --->
    fun onSongsListEvent(event: SongsListEvent){
        when(event){
            SongsListEvent.onPause -> {
                TODO()
            }
            SongsListEvent.onPlay -> {
                TODO()
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
                    _songsList.emit(documents.toObjects(Song::class.java))
                }
                //to check if we are getting the songs ----->
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


}