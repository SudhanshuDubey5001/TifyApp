package com.sudhanshu.spotifyclone.ui.songslist

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.data.entities.Song

@Composable
fun SongsListItem(
    song: Song,
    modifier: Modifier,
    isMediaControlVisible: Boolean,
//    isPausePlayClicked: MutableState<Boolean>,
    viewModel: SongsListViewModel
) {
    val pausePlay = viewModel.isPausePlayClicked.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        AsyncImage(
            model = song.imageURL,
            contentDescription = "song cover image",
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Column(
            modifier = modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = song.title,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    color = Color.White,
                )
            )
            Text(
                text = song.subtitle,
                style = TextStyle(
                    color = Color.White,
                )
            )
        }

        if (isMediaControlVisible) {
            IconButton(
                onClick = {
                    viewModel.onPlayerEvents(PlayerEvents.onPausePlay)
                },
            ) {
                if (!pausePlay.value) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "play button",
                        tint = Color.White
                    )
                } else Icon(
                    painterResource(id = R.drawable.pausebutton_unfilled),
                    contentDescription = "pause button",
                    tint = Color.White
                )
            }
        }
    }
}