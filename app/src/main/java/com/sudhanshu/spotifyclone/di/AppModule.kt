package com.sudhanshu.spotifyclone.di

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sudhanshu.spotifyclone.other.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
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
        @ApplicationContext context: Context): Player {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun providesMediaSession(
        @ApplicationContext context: Context,
        player: Player
    ): MediaSession {
        return MediaSession.Builder(context, player)
            .build()
    }

//    @Provides
//    fun providesApplicationContext(
//        @ApplicationContext context: Context
//    ): Context{
//        return context
//    }
}