package com.merteroglu286.auth.data.mapper

import com.merteroglu286.auth.data.responses.LoginResponse
import com.merteroglu286.auth.domain.model.User

interface LoginMapper {
    suspend fun toDomain(loginResponse: LoginResponse) : User
}