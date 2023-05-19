package com.sudhanshu.spotifyclone.di

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.sudhanshu.spotifyclone.data.entities.Song
import com.sudhanshu.spotifyclone.other.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesSongCollectionReference(): CollectionReference {
        return Firebase.firestore.collection(Constants.SONG_COLLECTION)
    }

    @Provides
    @Singleton
    fun providesExoPlayer(
        @ApplicationContext context: Context): ExoPlayer{
        return ExoPlayer.Builder(context).build()
    }
}