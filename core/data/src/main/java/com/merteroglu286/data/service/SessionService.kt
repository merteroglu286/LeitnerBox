package com.merteroglu286.data.service

import com.merteroglu286.data.response.TokenResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header

const val BASE_URL = "https://example.com"
const val REFRESH_TOKEN = "refreshToken"

interface SessionService {

    @GET("$BASE_URL/Auth/GetSession")
    suspend fun getTokens(
        @Header(REFRESH_TOKEN) refreshToken: String
    ): Response<TokenResponse>

    @DELETE("$BASE_URL/Auth/DeleteSession")
    suspend fun logout(
        @Header(REFRESH_TOKEN) refreshToken: String
    ): Response<Unit>
}