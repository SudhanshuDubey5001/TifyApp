package com.sudhanshu.spotifyclone.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.sudhanshu.spotifyclone.data.remote.SongsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module //we use this tag bcz this is a dagger hilt module
@InstallIn(ServiceComponent::class)   //we provide a servicecomponent instance so that all the modules inside DI lives as long the service is running
object ServiceModule {
    //there are other components you can use like activityComponent, fragmentComponent etc. Just to define the lifecycle of
    //module.

    @ServiceScoped  //this will make single instances of this dependency in service (equivalent of singleton)
    @Provides   //to instruct Hilt that this function will providing something
    fun providesAudioAttributes() = AudioAttributes.Builder().apply {
        setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        setUsage(C.USAGE_MEDIA)
    }.build()

    //now for the instance of exoplayer
    @ServiceScoped
    @Provides
    fun providesMedia3Exoplayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ) = ExoPlayer.Builder(context).build().apply {
        setAudioAttributes(audioAttributes, true)
        setHandleAudioBecomingNoisy(true)
    }

    //now we need to provide the source of data for the player so that it knows where to play from
    @ServiceScoped
    @Provides
    fun providesDataSourceFactory(
        @ApplicationContext context: Context
    ) = DefaultDataSource.Factory(context)

    @ServiceScoped
    @Provides
    fun providesSongsDatabase() = SongsDatabase()
}