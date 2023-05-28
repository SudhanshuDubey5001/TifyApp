package com.sudhanshu.spotifyclone.ui.songslist

import com.sudhanshu.spotifyclone.data.entities.Song

sealed class PlayerEvents {
    data class onPlayNewSong(val song: Song): PlayerEvents()
    object onPausePlay: PlayerEvents()
    object onplayPreviousSong: PlayerEvents()
    object onplayNextSong: PlayerEvents()
    object onShuffleClick: PlayerEvents()
    object onRepeatClick: PlayerEvents()
    data class onseekMusicDone(val value: Float): PlayerEvents()
    data class onChangeColorGradient(val url: String): PlayerEvents()
}