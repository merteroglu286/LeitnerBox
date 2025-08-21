package com.merteroglu286.data.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("errorCode")
    val errorCode: String?,
    @SerializedName("errorMessage")
    val errorMessage: String?,
    @SerializedName("errorFieldList")
    val errorFieldList: List<String>?
)
