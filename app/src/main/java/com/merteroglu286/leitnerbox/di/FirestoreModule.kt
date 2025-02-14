package com.merteroglu286.leitnerbox.di

import com.google.firebase.firestore.FirebaseFirestore
import com.merteroglu286.leitnerbox.data.repository.FirestoreRepositoryImpl
import com.merteroglu286.leitnerbox.domain.model.User
import com.merteroglu286.leitnerbox.domain.repository.FirestoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestoreRepository(
        firestore: FirebaseFirestore
    ): FirestoreRepository {
        return FirestoreRepositoryImpl(firestore)
    }
}
