package com.merteroglu286.auth.data.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("username")
    val username: String?
)
