package com.sudhanshu.spotifyclone.ui.songslist

import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudhanshu.spotifyclone.data.entities.Song

@androidx.media3.common.util.UnstableApi
@Composable
fun SongsListScreen(
    viewModel: SongsListViewModel = hiltViewModel()
) {

    var song: Song

    val songsList = songsListFlow.collectAsState()

    /* ------------Songs list UI-----------*/
    val isPausePlayClicked = remember {
        mutableStateOf(true)
    }
    val isBottomCardShowing = remember {
        mutableStateOf(false)
    }
    var currentSong = remember {
        mutableStateOf(Song())
    }
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
                                isPausePlayClicked.value = !isPausePlayClicked.value
                                viewModel.onSongsListEvent(SongsListEvent.onPlayNewSong(it))
                                currentSong.value = it
                                isBottomCardShowing.value = true
                            },
                            isMediaControlVisible = false,
                            isPausePlayClicked = isPausePlayClicked,
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
                    isPausePlayClicked = isPausePlayClicked,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun BottomCardMediaPlayer(
    song: Song,
    modifier: Modifier,
    isPausePlayClicked: MutableState<Boolean>,
    viewModel: SongsListViewModel
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            colors = CardDefaults.cardColors(Color.Gray)
        ) {
            SongsListItem(
                song = song,
                isMediaControlVisible = true,
                isPausePlayClicked = isPausePlayClicked,
                modifier = Modifier.clickable {
                    // TODO: implement logic to open fragment for controlling current song
                },
                viewModel = viewModel
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