package com.merteroglu286.leitnerbox.di

import android.content.Context
import com.merteroglu286.leitnerbox.utility.manager.MediaManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    fun provideMediaManager(@ApplicationContext context: Context): MediaManager {
        return MediaManager(context)
    }
}