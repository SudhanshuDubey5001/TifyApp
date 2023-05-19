package com.sudhanshu.spotifyclone.ui.songslist

import android.os.Looper.prepare
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import com.sudhanshu.spotifyclone.data.entities.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@androidx.media3.common.util.UnstableApi
@Composable
fun SongsListScreen(
//    viewModel: SongsListViewModel = hiltViewModel()
) {

    val samplesong = Song(
        title = "Wish You Were Here",
        subtitle = "Pink Floyd",
        mediaID = "31",
        imageURL = "https://www.emp.co.uk/dw/image/v2/BBQV_PRD/on/demandware.static/-/Sites-master-emp/default/dw9b271e73/images/3/5/9/3/359321a-emp.jpg?sw=1000&sh=800&sm=fit&sfrm=png",
        songURL = "songURL"
    )

    val songsList = songsListFlow.collectAsState()
    val context = LocalContext.current

    val player = ExoPlayer.Builder(context).build()
    val playerControlView = PlayerControlView(context)
    playerControlView.player = player

    val mediaItem = MediaItem
        .Builder()
        .setUri("https://firebasestorage.googleapis.com/v0/b/tify-a4d90.appspot.com/o/004.the_lumineers_-_ho_hey.mp3?alt=media&token=b6973b88-aba5-4800-9659-17ea15ae52f0")
        .build()

    player.apply {
        setMediaItem(mediaItem)
        playWhenReady = false
        prepare()
    }

    /* ------------Songs list UI-----------*/
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(songsList.value) {
                        SongsListItem(song = it, Modifier.clickable {
                            player.play()
                        })
                    }
                }

                AndroidView(factory = { playerControlView })

            }
            Box{
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(8.dp),
                    colors = CardDefaults.cardColors(Color.Gray)
                ){
                    SongsListItem(song = samplesong, modifier = Modifier.clickable {
                        // TODO: implement logic to open fragment for controlling current song
                    })
                }
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Preview
@Composable
fun previewSongsListScreen() {
    SongsListScreen()
}