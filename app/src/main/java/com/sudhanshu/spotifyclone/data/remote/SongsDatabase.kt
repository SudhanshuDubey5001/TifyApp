package com.sudhanshu.spotifyclone.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants.SONG_COLLECTION
import kotlinx.coroutines.tasks.await

class SongsDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    //now we will make a network call to get all the songs in the list
    suspend fun getSongCollection(): List<Song> {
        return try {
//            await gives "Any" type object so we converted it to Song object
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception){
            emptyList()
        }
    }
}