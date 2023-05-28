package com.sudhanshu.spotifyclone.ui.songslist.compose

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants.LOG
import com.sudhanshu.spotifyclone.ui.songslist.GifImage
import com.sudhanshu.spotifyclone.ui.songslist.PlayerEvents
import com.sudhanshu.spotifyclone.ui.songslist.SongsListViewModel

@Composable
fun SwipeUpPlayer(
    song: Song,
    viewModel: SongsListViewModel,
    colorList: List<Color>
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //fetch the color gradient from cover images whenever the song changes or new song plays
        viewModel.onPlayerEvents(PlayerEvents.onChangeColorGradient(song.imageURL))
        //setup blurred or gradient background based on your financial worth :P [no offence....cuz its true xD]
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            blurBackground(
                imageURL = song.imageURL,
                colorList = colorList
            )
        } else {
            gradientBackground(colorList = colorList)
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .background(Color.Transparent)
                        .fillMaxSize()
                ) {
                    AsyncImage(
                        modifier = Modifier.align(Alignment.Center),
                        contentScale = ContentScale.Fit,
                        model = song.imageURL,
                        contentDescription = "",
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .background(Color.Transparent)
                        .fillMaxWidth()
                ) {
                    PlayerControls(song, viewModel)
                }
            }
        }
    }
}

@Composable
fun PlayerControls(
    song: Song,
    viewModel: SongsListViewModel
) {
    val currentMediaPosition = viewModel.currentMediaPosition.collectAsState()
    val currentMediaDurationInMinutes = viewModel.currentSongDurationInMinutes.collectAsState()
    val currentMediaProgressInMinutes = viewModel.currentSongProgressInMinutes.collectAsState()
    val isShuffleClicked = viewModel.isShuffleClicked.collectAsState()

//    var isShuffleEnabled = remember {
//        mutableStateOf(false)
//    }

    val isRepeatEnabled = remember {
        mutableStateOf(false)
    }

    var currentPos: Float = currentMediaPosition.value

    Column(modifier = Modifier.padding(25.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.jost_regular)),
                    color = Color.White
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = song.subtitle,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.jost_regular)),
                    color = Color.White
                ),
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            BuildShuffleSongIcon(modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
                isEnabled = isShuffleClicked.value,
                onClick = {
                    viewModel.onPlayerEvents(PlayerEvents.onShuffleClick)
                })

            Spacer(modifier = Modifier.width(10.dp))

            BuildSkipToPrevious(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .clickable {
                    viewModel.onPlayerEvents(PlayerEvents.onplayPreviousSong)
                })

            Spacer(modifier = Modifier.width(10.dp))

            BuildPausePlayLoadIconButtons(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        viewModel.onPlayerEvents(PlayerEvents.onPausePlay)
                    }, viewModel
            )

            Spacer(modifier = Modifier.width(10.dp))

            BuildSkipToNext(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .clickable {
                    viewModel.onPlayerEvents(PlayerEvents.onplayNextSong)
                })

            Spacer(modifier = Modifier.width(10.dp))

            BuildRepeatIconButton(modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
                isEnabled = isRepeatEnabled.value,
                onClick = {
                    isRepeatEnabled.value = !isRepeatEnabled.value
                    viewModel.onPlayerEvents(PlayerEvents.onRepeatClick)
                })
        }

        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            value = currentPos,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            ),
            onValueChange = {
                Log.d(LOG, "seek to --> $it")
                currentPos = it
            },
            onValueChangeFinished = {
                viewModel.onPlayerEvents(PlayerEvents.onseekMusicDone(currentPos))
            },
        )
        Row {
            Text(
                modifier = Modifier.weight(1f),
                color = Color.White,
                text = currentMediaProgressInMinutes.value,
                style = TextStyle(
                    fontSize = 12.sp
                ),
            )
            Text(
                color = Color.White,
                text = currentMediaDurationInMinutes.value,
                style = TextStyle(
                    fontSize = 12.sp
                ),
            )
        }
    }
}

@Composable
fun BuildShuffleSongIcon(
    modifier: Modifier,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier, onClick = onClick
    )
    {
        if (!isEnabled) {
            Icon(
                painter = painterResource(id = R.drawable.shuffle),
                contentDescription = "shuffle off",
                tint = Color.White
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.shuffle_on),
                contentDescription = "shuffle on",
                tint = Color.White
            )
        }
    }
}

@Composable
fun BuildRepeatIconButton(
    modifier: Modifier,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier, onClick = onClick
    )
    {
        if (!isEnabled) {
            Icon(
                painter = painterResource(id = R.drawable.repeat_one),
                contentDescription = "repeat off",
                tint = Color.White
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.repeat_one_on),
                contentDescription = "repeat on",
                tint = Color.White
            )
        }
    }
}

@Composable
fun BuildPausePlayLoadIconButtons(modifier: Modifier, viewModel: SongsListViewModel) {

    val isPausePlayClicked = viewModel.isPausePlayClicked.collectAsState()
    val isPlayerBuffering = viewModel.isPlayerBuffering.collectAsState()

    if (!isPlayerBuffering.value) {
        if (isPausePlayClicked.value) {
            Image(
                modifier = modifier,
                painter = painterResource(R.drawable.pausebutton),
                contentDescription = "pause",
                contentScale = ContentScale.Fit
            )
        } else {
            Image(
                modifier = modifier,
                painter = painterResource(R.drawable.playbutton),
                contentDescription = "play",
                contentScale = ContentScale.Fit
            )
        }
    } else {
        GifImage(data = R.drawable.loading_big)
    }
}

@Composable
fun BuildSkipToPrevious(modifier: Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.skippreviousbutton),
        contentDescription = "skip to previous song",
        contentScale = ContentScale.Fit
    )
}

@Composable
fun BuildSkipToNext(modifier: Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.skipnextbutton),
        contentDescription = "skip to next song",
        contentScale = ContentScale.Fit
    )
}

@Composable
fun blurBackground(imageURL: String, colorList: List<Color>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(40.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            model =
            imageURL,
            contentDescription = "blurred background",
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorList[2],
                            colorList[3],
                        )
                    )
                )
        )
    }
}

@Composable
fun gradientBackground(colorList: List<Color>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = colorList
                )
            )
    )
}