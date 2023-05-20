package com.sudhanshu.spotifyclone.ui.songslist

import com.sudhanshu.spotifyclone.data.entities.Song

sealed class SongsListEvents {
    data class onPlayNewSong(val song: Song): SongsListEvents()
    object onPausePlay: SongsListEvents()
    object onPlayerClick: SongsListEvents()
    object onplayPreviousSong: SongsListEvents()
    object onplayNextSong: SongsListEvents()
}