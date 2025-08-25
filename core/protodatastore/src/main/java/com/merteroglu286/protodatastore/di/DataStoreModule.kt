package com.merteroglu286.protodatastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.merteroglu286.proto.Preferences
import com.merteroglu286.proto.Session
import com.merteroglu286.protodatastore.factory.preferencesDataStore
import com.merteroglu286.protodatastore.factory.sessionDataStore
import com.merteroglu286.protodatastore.manager.preferences.PreferencesDataStoreImpl
import com.merteroglu286.protodatastore.manager.preferences.PreferencesDataStoreInterface
import com.merteroglu286.protodatastore.manager.session.SessionDataStoreImpl
import com.merteroglu286.protodatastore.manager.session.SessionDataStoreInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideSessionDataStore(@ApplicationContext context: Context): DataStore<Session> {
        return context.sessionDataStore
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.preferencesDataStore
    }

    @Provides
    @Singleton
    fun provideSessionDataStoreManager(sessionDataStore: DataStore<Session>) : SessionDataStoreInterface {
        return SessionDataStoreImpl(sessionDataStore)
    }

    @Provides
    @Singleton
    fun providePreferencesDataStoreManager(preferencesDataStore: DataStore<Preferences>) : PreferencesDataStoreInterface {
        return PreferencesDataStoreImpl(preferencesDataStore)
    }

}