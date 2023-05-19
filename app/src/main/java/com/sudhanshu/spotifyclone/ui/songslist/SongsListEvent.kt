package com.sudhanshu.spotifyclone.ui.songslist

import com.sudhanshu.spotifyclone.data.entities.Song

sealed class SongsListEvent {
    data class onPlayNewSong(val song: Song): SongsListEvent()
    object onPausePlay: SongsListEvent()
    object onPlayerClick: SongsListEvent()
}