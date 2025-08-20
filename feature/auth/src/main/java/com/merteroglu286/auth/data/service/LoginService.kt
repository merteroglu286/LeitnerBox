package com.merteroglu286.auth.data.service

import com.merteroglu286.auth.data.requests.LoginRequest
import com.merteroglu286.auth.data.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

const val BASE_URL = "https//mydomain.com"
interface LoginService {

    @POST("$BASE_URL/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}