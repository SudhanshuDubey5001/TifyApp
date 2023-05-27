package com.sudhanshu.spotifyclone.ui.songslist

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants.LOG
import com.sudhanshu.spotifyclone.ui.songslist.compose.BottomCardMediaPlayer
import com.sudhanshu.spotifyclone.ui.songslist.compose.SwipeUpPlayer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsListScreen(
    viewModel: SongsListViewModel = hiltViewModel()
) {

    val songsList = viewModel.songsListFlow.collectAsState()
    val currentSong = viewModel.currentSongFlow.collectAsState()
    val gradientColorList = gradientColorList.collectAsState()

    /* ------------Songs list UI-----------*/
    val isBottomCardShowing = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val shuffleIconColor = remember {
        mutableStateOf(Color.White)
    }
    val bottomSheetState =
        rememberBottomSheetScaffoldState(bottomSheetState = sheetState)


    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = 0.dp,
        sheetContainerColor = gradientColorList.value.get(0),   //get the dominant color
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            SwipeUpPlayer(
                currentSong.value,
                viewModel,
                gradientColorList.value
            )
        }) {
        Box(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        gradientColorList.value[2],
                        gradientColorList.value[3]
                    )
                )
            )
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 60.dp)
                            .weight(1f),
                        text = "Songs",
                        style = TextStyle(
                            fontSize = 60.sp,
                            fontFamily = FontFamily(Font(R.font.jost_regular)),
                            color = Color.White
                        )
                    )
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .padding(horizontal = 30.dp),
                        onClick = {
                            if (shuffleIconColor.value == Color.White)
                                shuffleIconColor.value = Color.Magenta else Color.White
                            viewModel.onPlayerEvents(PlayerEvents.onShuffleClick)
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.shuffle),
                            contentDescription = "shuffle",
                            tint = shuffleIconColor.value
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(songsList.value) {
                        SongsListItem(
                            song = it,
                            Modifier.clickable {
                                viewModel.onPlayerEvents(PlayerEvents.onPlayNewSong(it))
                                isBottomCardShowing.value = true
                                it.isBackgroundColorEnabled = true
                            },
                            isMediaControlVisible = false,
                            viewModel = viewModel,
                        )
                    }
                }
            }

            /** --------Bottom Media controller UI------------- **/
            if (isBottomCardShowing.value) {
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    BottomCardMediaPlayer(
                        modifier = Modifier,
                        song = currentSong.value,
                        viewModel = viewModel,
                        sheetState = sheetState,
                        cardColor = gradientColorList.value[0] //muted color
                    )
                }
            }
        }
    }
}