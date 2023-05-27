package com.sudhanshu.spotifyclone.ui.songslist

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.sudhanshu.spotifyclone.R
import com.sudhanshu.spotifyclone.data.entities.Song
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun SongsListItem(
    song: Song,
    modifier: Modifier,
    isMediaControlVisible: Boolean,
    viewModel: SongsListViewModel
) {
    val pausePlay = viewModel.isPausePlayClicked.collectAsState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(getColor(value = song.isBackgroundColorEnabled))
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
        } else {
            if (song.isBackgroundColorEnabled) {
                if (pausePlay.value) GifImage(R.drawable.smallgif) else GifImage(R.drawable.loading)

            }
        }
    }
}

@Composable
fun getColor(value: Boolean): Color {
    return if (value) Color.Black else Color.Transparent
}

@Composable
fun GifImage(data: Any?) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
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