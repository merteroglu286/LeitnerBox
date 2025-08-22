package com.merteroglu286.auth.data.source

import com.merteroglu286.auth.data.requests.LoginRequest
import com.merteroglu286.auth.domain.model.User
import com.merteroglu286.data.result.OutCome

interface LoginRemote {
    suspend fun login(loginRequest: LoginRequest): OutCome<User>
}