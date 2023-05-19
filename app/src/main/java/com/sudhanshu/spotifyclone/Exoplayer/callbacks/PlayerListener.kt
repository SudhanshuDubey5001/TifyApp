package com.sudhanshu.spotifyclone.Exoplayer.callbacks

import android.util.Log
import androidx.media3.common.Player
import com.sudhanshu.spotifyclone.other.Constants.LOG
import javax.inject.Inject

class PlayerListener (
    private val player: Player
) : Player.Listener {

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
            }
        }
    }

    //the player has a playWhenReady flag to indicate the user intention to play.
// Changes in this flag can be received by implementing
    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        Log.d(LOG, "Player: PlaywhenReadyChanged -> "+playWhenReady)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if(isPlaying){

        }else{
            
        }
    }
}