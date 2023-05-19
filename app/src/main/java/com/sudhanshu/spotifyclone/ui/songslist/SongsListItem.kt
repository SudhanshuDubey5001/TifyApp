package com.sudhanshu.spotifyclone.ui.songslist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sudhanshu.spotifyclone.data.entities.Song

@Composable
fun SongsListItem(
    song: Song,
    modifier: Modifier
) {
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
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = song.subtitle,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun previewItem() {
    SongsListItem(
        Song(
            title = "Wanna talk to you",
            subtitle = "Iron Maiden",
            mediaID = "22",
            imageURL = "https://upload.wikimedia.org/wikipedia/en/7/7c/Iron_Maiden_%28album%29_cover.jpg",
            songURL = "somgURL"
        ),
        Modifier.fillMaxSize()
    )
}