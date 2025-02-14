package com.merteroglu286.leitnerbox.di

import com.google.firebase.auth.FirebaseAuth
import com.merteroglu286.leitnerbox.data.repository.FirebaseAuthRepositoryImpl
import com.merteroglu286.leitnerbox.domain.repository.FirebaseAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthRepository(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthRepository {
        return FirebaseAuthRepositoryImpl(firebaseAuth)
    }
}