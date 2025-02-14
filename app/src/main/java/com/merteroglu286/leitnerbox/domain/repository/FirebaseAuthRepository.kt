package com.merteroglu286.leitnerbox.domain.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthRepository {
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    suspend fun logout()
    fun getCurrentUser(): FirebaseUser?
    suspend fun signInWithGoogle(credential: AuthCredential): Result<FirebaseUser>
    suspend fun resetPassword(email: String): Result<Boolean>
}