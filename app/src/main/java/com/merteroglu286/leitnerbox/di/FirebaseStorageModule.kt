package com.merteroglu286.leitnerbox.di

import com.google.firebase.storage.FirebaseStorage
import com.merteroglu286.leitnerbox.data.repository.FirebaseStorageRepositoryImpl
import com.merteroglu286.leitnerbox.domain.repository.FirebaseStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseStorageModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageRepository(
        firebaseStorage: FirebaseStorage
    ): FirebaseStorageRepository {
        return FirebaseStorageRepositoryImpl(firebaseStorage)
    }
}
