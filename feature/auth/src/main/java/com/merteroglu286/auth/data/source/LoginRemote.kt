package com.merteroglu286.auth.data.source

import com.merteroglu286.auth.domain.model.User
import com.merteroglu286.domain.result.OutCome

interface LoginRemote {
    suspend fun login(username: String, password: String): OutCome<User>
}