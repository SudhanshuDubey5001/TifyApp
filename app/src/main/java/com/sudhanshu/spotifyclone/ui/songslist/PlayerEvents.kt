package com.sudhanshu.spotifyclone.ui.songslist

import com.sudhanshu.spotifyclone.data.entities.Song

sealed class PlayerEvents {
    data class onPlayNewSong(val song: Song): PlayerEvents()
    object onPausePlay: PlayerEvents()
    object onPlayerClick: PlayerEvents()
    object onplayPreviousSong: PlayerEvents()
    object onplayNextSong: PlayerEvents()
    data class onChangeColorGradient(val url: String): PlayerEvents()
}