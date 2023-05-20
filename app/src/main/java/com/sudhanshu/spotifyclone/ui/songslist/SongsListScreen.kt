package com.sudhanshu.spotifyclone.ui.songslist

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants.LOG

@androidx.media3.common.util.UnstableApi
@Composable
fun SongsListScreen(
    viewModel: SongsListViewModel = hiltViewModel()
) {

    val songsList = viewModel.songsListFlow.collectAsState()
    val currentSong = viewModel.currentSongFlow.collectAsState()

    /* ------------Songs list UI-----------*/
    val isBottomCardShowing = remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(songsList.value) {
                        SongsListItem(
                            song = it,
                            Modifier.clickable {
                                viewModel.onSongsListEvent(SongsListEvents.onPlayNewSong(it))
//                                viewModel.onSongsListEvent(SongsListEvents.updateCurrentSong(it))
                                isBottomCardShowing.value = true
                            },
                            isMediaControlVisible = false,
                            viewModel = viewModel
                        )
                    }
                }
            }

            /** --------Bottom Media controller UI------------- **/
            if (isBottomCardShowing.value) {
                BottomCardMediaPlayer(
                    song = currentSong.value,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Composable
fun BottomCardMediaPlayer(
    song: Song,
    modifier: Modifier,
    viewModel: SongsListViewModel,
) {
    val x = remember { mutableStateOf(0f) }
    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .draggable(
                    state = rememberDraggableState(onDelta = { delta ->
                        Log.d(LOG, "Dragged: $delta")
                        x.value = delta
                    }),
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        Log.d(LOG, "Dragged Completed")
                        when {
                            x.value > 0 -> { //right swipe
                                viewModel.onSongsListEvent(SongsListEvents.onplayPreviousSong)
                            }
                            x.value < 0 -> { //left swipe
                                viewModel.onSongsListEvent(SongsListEvents.onplayNextSong)
                            }
                        }
                    }
                ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            colors = CardDefaults.cardColors(Color.Gray)
        ) {
            SongsListItem(
                song = song,
                isMediaControlVisible = true,
                modifier = Modifier.clickable {
                    // TODO: implement logic to open fragment for controlling current song
                },
                viewModel = viewModel,
            )
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Preview
@Composable
fun previewSongsListScreen() {
    SongsListScreen()
}