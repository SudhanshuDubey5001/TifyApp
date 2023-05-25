package com.sudhanshu.spotifyclone

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sudhanshu.spotifyclone.Exoplayer.ExoplayerJobs
import com.sudhanshu.spotifyclone.Exoplayer.ExoplayerService
import com.sudhanshu.spotifyclone.other.Constants.LOG
import com.sudhanshu.spotifyclone.ui.RootCompose
import com.sudhanshu.spotifyclone.ui.songslist.SongsListScreen
import com.sudhanshu.spotifyclone.ui.songslist.SongsListViewModel
import com.sudhanshu.spotifyclone.ui.theme.SpotifyCloneTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var intentOfService: Intent
    override fun onStart() {
        super.onStart()
        //start the service for music notification player
        intentOfService = Intent(this, ExoplayerService::class.java)
        startService(intentOfService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotifyCloneTheme {
                SongsListScreen()
            }
        }
    }

    override fun onDestroy() {
        Log.d(LOG, "Activity onDestroy called")
        stopService(intentOfService)
        cacheDir.delete()
        super.onDestroy()
        exitProcess(0)
    }
}