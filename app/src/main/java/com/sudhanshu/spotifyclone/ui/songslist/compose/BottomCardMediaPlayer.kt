package com.sudhanshu.spotifyclone.ui.songslist.compose

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants
import com.sudhanshu.spotifyclone.ui.songslist.PlayerEvents
import com.sudhanshu.spotifyclone.ui.songslist.SongsListItem
import com.sudhanshu.spotifyclone.ui.songslist.SongsListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomCardMediaPlayer(
    song: Song,
    cardColor: Color,
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
                        Log.d(Constants.LOG, "Drag: " + delta)
                    }),
                    onDragStopped = {
                        Log.d(Constants.LOG, "Dragged Completed")
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
            colors = CardDefaults.cardColors(cardColor)
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