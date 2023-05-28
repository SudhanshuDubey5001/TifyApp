package com.sudhanshu.spotifyclone.ui.songslist

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.ui.songslist.compose.BottomCardMediaPlayer
import com.sudhanshu.spotifyclone.ui.songslist.compose.BuildShuffleSongIcon
import com.sudhanshu.spotifyclone.ui.songslist.compose.SwipeUpPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsListScreen(
    viewModel: SongsListViewModel = hiltViewModel()
) {

    val songsList = viewModel.songsListFlow.collectAsState()
    val currentSong = viewModel.currentSongFlow.collectAsState()
    val gradientColorList = gradientColorList.collectAsState()
    val isShuffleClicked = viewModel.isShuffleClicked.collectAsState()

    /* ------------Songs list UI-----------*/
    val isBottomCardShowing = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
//    val isShuffleEnable = remember {
//        mutableStateOf(false)
//    }
    val bottomSheetState =
        rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = 0.dp,
        sheetContainerColor = gradientColorList.value.get(3),   //get the darker color
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
                        gradientColorList.value[1],
                        gradientColorList.value[2],
                        gradientColorList.value[3]
                    )
                )
            )
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 30.dp, top = 40.dp, end = 0.dp, bottom = 10.dp)
                        .weight(1f),
                    text = "Songs",
                    style = TextStyle(
                        fontSize = 60.sp,
                        fontFamily = FontFamily(Font(R.font.jost_regular)),
                        color = Color.White
                    )
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .height(3.dp)
                            .border(1.dp, Color.White)
                    )
                    BuildShuffleSongIcon(
                        modifier = Modifier
                            .padding(horizontal = 20.dp),
                        isEnabled = isShuffleClicked.value,
                        onClick = {
                            viewModel.onPlayerEvents(PlayerEvents.onShuffleClick)
                        }
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(3f)
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

                /** --------Bottom Media controller UI------------- **/
                if (isBottomCardShowing.value) {
                    Box(
//                        modifier = Modifier.align(Alignment.BottomEnd)
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
}

@Composable
fun GifImage(data: Any?) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = data).apply(
                block = { size(Size.ORIGINAL) }
            )
                .build(),
            imageLoader = imageLoader,
        ),
        contentDescription = null,
    )
}