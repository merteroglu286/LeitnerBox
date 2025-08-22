package com.merteroglu286.auth.domain.mapper

import com.merteroglu286.auth.data.responses.LoginResponse
import com.merteroglu286.auth.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LoginMapperImpl(private val defaultDispatcher: CoroutineDispatcher) : LoginMapper {
    override suspend fun toDomain(loginResponse: LoginResponse): User {
        return withContext(defaultDispatcher) {
            User(
                id = loginResponse.id.orEmpty(),
                username = loginResponse.username.orEmpty()
            )
        }
    }
}