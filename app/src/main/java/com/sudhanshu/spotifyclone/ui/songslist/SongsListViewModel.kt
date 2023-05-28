package com.sudhanshu.spotifyclone.ui.songslist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.*
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.CollectionReference
import com.sudhanshu.spotifyclone.Exoplayer.ExoplayerJobs
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants.LOG
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private var _gradientColorList =
    MutableStateFlow(listOf(Color.Black, Color.DarkGray, Color.Red, Color.Black, Color.Black))
val gradientColorList = _gradientColorList.asStateFlow()

@HiltViewModel
class SongsListViewModel @Inject constructor(
    private val player: Player,
    private val songCollection: CollectionReference,
    @ApplicationContext context: Context
) : ViewModel() {
    //variables connected to view
    private var _songsList = MutableStateFlow(listOf<Song>())
    val songsListFlow = _songsList.asStateFlow()

    private var _currentSong = MutableStateFlow(Song())
    val currentSongFlow = _currentSong.asStateFlow()

    private var _currentMediaPosition = MutableStateFlow<Float>(0f)
    val currentMediaPosition = _currentMediaPosition.asStateFlow()

    private var _isPausePlayClicked = MutableStateFlow(false)
    val isPausePlayClicked = _isPausePlayClicked.asStateFlow()

    private var _isPlayerBuffering = MutableStateFlow(false)
    val isPlayerBuffering = _isPlayerBuffering.asStateFlow()

    private var _isShuffleClicked = MutableStateFlow(false)
    val isShuffleClicked = _isShuffleClicked.asStateFlow()

    private var _currentSongDurationInMinutes = MutableStateFlow("0:00")
    val currentSongDurationInMinutes = _currentSongDurationInMinutes.asStateFlow()

    private var _currentSongProgressInMinutes = MutableStateFlow("0:00")
    val currentSongProgressInMinutes = _currentSongProgressInMinutes.asStateFlow()

    var isShuffleClick = false

    //variables used only here
    private lateinit var songsList: List<Song>

    private val vms = viewModelScope
    private val exoplayerInstance: ExoplayerJobs = ExoplayerJobs(
        player,
        _currentSong,
        _currentMediaPosition,
        _currentSongDurationInMinutes,
        _currentSongProgressInMinutes,
        _isPausePlayClicked,
        _isPlayerBuffering,
        _isShuffleClicked,
        vms,
    )

    private var glideInstance: RequestManager

    init {
        glideInstance = Glide.with(context)
        player.addListener(exoplayerInstance)
        getSongsCollection()
        exoplayerInstance.setupMediaNotification(context)
    }

    override fun onCleared() {
        exoplayerInstance.performReleaseInstances() //just in case if Hilt is unable to for some unknown reasons
        Log.d(LOG, "onCleared called!!")
        super.onCleared()
    }

    //possible events --->
    fun onPlayerEvents(event: PlayerEvents) {
        when (event) {
            PlayerEvents.onPausePlay -> {
                exoplayerInstance.performPausePlay()
            }
            is PlayerEvents.onPlayNewSong -> {
                exoplayerInstance.performPlayNewSong(event.song)

                //everytime we need to refresh the playlist whenever user clicks a new song
                viewModelScope.launch {
                    exoplayerInstance.performPreparePlaylist(songsList)
                }
            }
            PlayerEvents.onplayNextSong -> {
                if(player.hasNextMediaItem()) exoplayerInstance.performPlayNextSong()
                else {
                    exoplayerInstance.performPreparePlaylist(songsList)
                }
            }
            PlayerEvents.onplayPreviousSong -> {
                if(player.hasPreviousMediaItem()) exoplayerInstance.performPlayPreviousSong()
            }
            is PlayerEvents.onChangeColorGradient -> {
                viewModelScope.launch {
                    getColorPalette(event.url)
                }
            }
            is PlayerEvents.onseekMusicDone -> {
                exoplayerInstance.onSeekMusicDone(event.value)
            }
            PlayerEvents.onShuffleClick -> {
                exoplayerInstance.shuffleClick()
            }
            PlayerEvents.onRepeatClick -> {
                exoplayerInstance.repeatClick()
            }
        }
    }

    //now we will make a network call to get all the songs in the list
    fun getSongsCollection() {
        viewModelScope.launch {
            songCollection.get().addOnSuccessListener { documents ->
                if (documents != null) {
                    songsList = documents.toObjects(Song::class.java)
                    _songsList.value = songsList.toList()

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

    fun getColorPalette(url: String) {
        glideInstance
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val palette = Palette.from(resource).generate()
                    //set the color palette
                    val color1 = Color(palette.getMutedColor(Color.Black.hashCode()))
                    val color2 = Color(palette.getVibrantColor(Color.Gray.hashCode()))
                    val color3 = Color(palette.getDarkMutedColor(Color.Black.hashCode()))
                    val color4 = Color(palette.getDarkVibrantColor(Color.Black.hashCode()))
                    _gradientColorList.value = listOf(color1, color2, color3, color4)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit
            })
    }


}