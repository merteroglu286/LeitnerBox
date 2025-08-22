package com.merteroglu286.auth.data.source

import com.merteroglu286.auth.data.requests.LoginRequest
import com.merteroglu286.auth.data.service.LoginService
import com.merteroglu286.auth.domain.mapper.LoginMapper
import com.merteroglu286.auth.domain.model.User
import com.merteroglu286.data.error.toDomain
import com.merteroglu286.data.result.OutCome
import com.merteroglu286.data.source.NetworkDataSource

class LoginRemoteImpl(
    private val networkDataSource: NetworkDataSource<LoginService>,
    private val loginMapper: LoginMapper
) : LoginRemote {
    override suspend fun login(loginRequest: LoginRequest): OutCome<User> {
        return networkDataSource.performRequest(
            request = { login(loginRequest) },
            onSuccess = { response, _ -> OutCome.success(loginMapper.toDomain(response)) },
            onError = { errorResponse, code -> OutCome.error(errorResponse.toDomain(code)) }
        )
    }
}