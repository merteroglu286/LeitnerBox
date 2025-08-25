package com.merteroglu286.protodatastore.manager.session

import com.merteroglu286.proto.Session
import kotlinx.coroutines.flow.Flow

interface SessionDataStoreInterface {

    // setter functions

    suspend fun setAccessToken(accessToken: String)
    suspend fun setRefreshToken(refreshToken: String)
    suspend fun setUserId(userID: String)

    suspend fun setSession(accessToken: String, refreshToken: String, userID: String)

    // getters

    suspend fun getAccessToken(): String
    fun getAccessTokenFlow(): Flow<String>

    suspend fun getRefreshToken(): String
    fun getRefreshTokenFlow(): Flow<String>

    suspend fun getUserId(): String
    fun getUserIdFlow(): Flow<String>


    suspend fun getSession(): Session
    fun getSessionFlow(): Flow<Session>

}