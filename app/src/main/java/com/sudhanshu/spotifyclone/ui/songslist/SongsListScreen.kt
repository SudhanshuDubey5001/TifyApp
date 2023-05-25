package com.sudhanshu.spotifyclone.ui.songslist

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants.LOG
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsListScreen(
    viewModel: SongsListViewModel = hiltViewModel()
) {

    val songsList = viewModel.songsListFlow.collectAsState()
    val currentSong = viewModel.currentSongFlow.collectAsState()
    val gradientColorList = viewModel.gradientColorList.collectAsState()

    /* ------------Songs list UI-----------*/
    val isBottomCardShowing = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val bottomSheetState =
        rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = 0.dp,
        sheetContainerColor = gradientColorList.value.get(0),   //get the dominant color
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            MusicPlayerControls(currentSong.value, viewModel, gradientColorList.value)
        }) {
        Box (modifier = Modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(
                    gradientColorList.value.get(1), gradientColorList.value.get(3)
                )
            )
        )){
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
                                viewModel.onPlayerEvents(PlayerEvents.onPlayNewSong(it))
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

            /** --------Bottom Media controller UI------------- **/
            if (isBottomCardShowing.value) {
                BottomCardMediaPlayer(
                    song = currentSong.value,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    viewModel = viewModel,
                    sheetState = sheetState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomCardMediaPlayer(
    song: Song,
    modifier: Modifier,
    viewModel: SongsListViewModel,
    sheetState: SheetState
) {
    val scope = rememberCoroutineScope()
    val x = remember { mutableStateOf(0f) }
    val y = remember { mutableStateOf(0f) }
    Box(modifier = modifier.clickable {
        scope.launch {
            sheetState.expand()
        }
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState(onDelta = { delta ->
                        y.value = delta
                    }),
                    onDragStopped = {
                        if (y.value < 0) sheetState.expand()
                    }
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState(onDelta = { delta ->
                        x.value = delta
                        Log.d(LOG, "Drag: " + delta)
                    }),
                    onDragStopped = {
                        Log.d(LOG, "Dragged Completed")
                        when {
                            x.value > 0 -> { //right swipe
                                viewModel.onPlayerEvents(PlayerEvents.onplayPreviousSong)
                            }
                            x.value < 0 -> { //left swipe
                                viewModel.onPlayerEvents(PlayerEvents.onplayNextSong)
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
                    scope.launch {
                        sheetState.expand()
                    }
                },
                viewModel = viewModel,
            )
        }

    }
}


@Composable
fun MusicPlayerControls(
    song: Song,
    viewModel: SongsListViewModel,
    colorList: List<Color>
) {
    val scope = rememberCoroutineScope()
    val sheetPullColor = remember { mutableStateOf(listOf(Color.Gray, Color.Black)) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        viewModel.onPlayerEvents(PlayerEvents.onChangeColorGradient(song.imageURL))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = colorList
                    )
                )
//            .blur(40.dp)
        ) {

//            AsyncImage(
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .fillMaxSize(),
//                contentScale = ContentScale.FillBounds,
//                model =
//                "https://images.genius.com/225d6cb0fef34cc080a51fefb2384c8b.599x607x1.jpg",
//                contentDescription = "",
//            )
        }
        Column {
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxSize()
            ) {
                AsyncImage(
                    modifier = Modifier.align(Alignment.Center),
                    contentScale = ContentScale.Fit,
                    model = song.imageURL,
                    contentDescription = "",
                    filterQuality = FilterQuality.High,
                )
            }
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "Song Title",
                        style = TextStyle(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Artist Name",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.White
                        ),
                    )
                }
            }
        }
    }
}

//@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
//@Preview
//@Composable
//fun previewSongsListScreen() {
//    MusicPlayerControls()
//}

@Composable
fun PlayerControls() {
    var isPlaying by remember { mutableStateOf(false) }
    var isShuffled by remember { mutableStateOf(false) }
    var isRepeatOn by remember { mutableStateOf(false) }

    BottomAppBar(
        containerColor = Color.Black,
        tonalElevation = 8.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.height(72.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            IconButton(
                onClick = { /* Handle previous song */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Previous Song",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { /* Handle next song */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Next Song",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { isShuffled = !isShuffled },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Shuffle",
                    tint = if (isShuffled) Color.Green else Color.White
                )
            }
            IconButton(
                onClick = { isRepeatOn = !isRepeatOn },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Repeat",
                    tint = if (isRepeatOn) Color.Green else Color.White
                )
            }
        }
    }
}