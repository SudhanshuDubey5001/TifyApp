package com.sudhanshu.spotifyclone.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

//@Module //we use this tag bcz this is a dagger hilt module
//@InstallIn(ServiceComponent::class)   //we provide a servicecomponent instance so that all the modules inside DI lives as long the service is running
object ServiceModule {
    //there are other components you can use like activityComponent, fragmentComponent etc. Just to define the lifecycle of
    //module.

    //    @ServiceScoped  //this will make single instances of this dependency in service (equivalent of singleton)
//    @Provides   //to instruct Hilt that this function will providing something
//    fun providesAudioAttributes() = AudioAttributes.Builder().apply {
//        setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
//        setUsage(C.USAGE_MEDIA)
//    }.build()
//
//    @ServiceScoped
//    @Provides
//    fun providesMediaSession(
//        @ApplicationContext context: Context,
//        player: Player
//    ): MediaSession {
//        return MediaSession.Builder(context, player).build()
//    }
}