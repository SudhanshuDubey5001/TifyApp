package com.sudhanshu.spotifyclone.ui.songslist

import com.sudhanshu.spotifyclone.data.entities.Song

sealed class SongsListEvents {
    data class onPlayNewSong(val song: Song): SongsListEvents()
    object onPausePlay: SongsListEvents()
    object onPlayerClick: SongsListEvents()

    data class updateCurrentSong(val song: Song): SongsListEvents()

    object playPreviousSong: SongsListEvents()
    object playNextSong: SongsListEvents()
}