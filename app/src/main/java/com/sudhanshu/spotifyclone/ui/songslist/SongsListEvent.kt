package com.sudhanshu.spotifyclone.ui.songslist

sealed class SongsListEvent {
    object onPlay: SongsListEvent()
    object onPause: SongsListEvent()
    object onPlayerClick: SongsListEvent()
}