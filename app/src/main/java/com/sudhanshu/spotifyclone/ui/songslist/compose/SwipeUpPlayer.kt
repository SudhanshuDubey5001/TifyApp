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

@Composable
fun PlayerControls(
    song: Song,
    viewModel: SongsListViewModel
) {
    val isPausePlayClicked = viewModel.isPausePlayClicked.collectAsState()
    val seekCurrentValue = viewModel.currentMediaPosition.collectAsState()
    val currentMediaDurationInMinutes = viewModel.currentSongDurationInMinutes.collectAsState()
    val currentMediaProgressInMinutes = viewModel.currentSongProgressInMinutes.collectAsState()

    val seek = remember {
        mutableStateOf(0f)
    }

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
            Image(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .clickable {
                        viewModel.onPlayerEvents(PlayerEvents.onplayNextSong)
                    },
                painter = painterResource(R.drawable.skippreviousbutton),
                contentDescription = "skipToPrevious",
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            if (isPausePlayClicked.value) {
                Image(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            viewModel.onPlayerEvents(PlayerEvents.onPausePlay)
                        },
                    painter = painterResource(R.drawable.pausebutton),
                    contentDescription = "pause",
                    contentScale = ContentScale.Fit
                )
            } else {
                Image(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            viewModel.onPlayerEvents(PlayerEvents.onPausePlay)
                        },
                    painter = painterResource(R.drawable.playbutton),
                    contentDescription = "play",
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .clickable {
                        viewModel.onPlayerEvents(PlayerEvents.onplayNextSong)
                    },
                painter = painterResource(R.drawable.skipnextbutton),
                contentDescription = "skipToPrevious",
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        if (seekCurrentValue.value == 0f) seek.value = 0f

        Slider(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            value = seek.value,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            ),
            onValueChange = {
                Log.d(LOG, "seek to --> $it")
                seek.value = it
//                seekCurrentValue.value = it
                viewModel.onPlayerEvents(PlayerEvents.onseekMusicDone(it))
            })
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