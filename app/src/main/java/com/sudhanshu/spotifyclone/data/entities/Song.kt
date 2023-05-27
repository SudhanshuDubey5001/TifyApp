package com.sudhanshu.spotifyclone.data.entities

data class Song(
    val title: String = "",
    val subtitle: String = "",
    val mediaID: String = "",
    val imageURL: String = "",
    val songURL: String = "",
    var isBackgroundColorEnabled: Boolean = false
)
