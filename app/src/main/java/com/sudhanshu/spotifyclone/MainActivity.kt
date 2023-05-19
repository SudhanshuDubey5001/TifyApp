package com.sudhanshu.spotifyclone

import android.os.Bundle
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
import com.sudhanshu.spotifyclone.ui.RootCompose
import com.sudhanshu.spotifyclone.ui.songslist.SongsListScreen
import com.sudhanshu.spotifyclone.ui.songslist.SongsListViewModel
import com.sudhanshu.spotifyclone.ui.theme.SpotifyCloneTheme

@UnstableApi class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotifyCloneTheme {
                SongsListScreen()
            }
        }
        val songDB = SongsListViewModel(this)
        songDB.getSongsCollection()
    }
}