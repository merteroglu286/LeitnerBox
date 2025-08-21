package com.merteroglu286.data.error

import com.google.gson.Gson
import com.merteroglu286.data.model.ErrorMessage
import com.merteroglu286.data.response.ErrorResponse

// mapping errorResponse to ErrorMessage model
fun ErrorResponse.toDomain(code: Int): ErrorMessage {
    return ErrorMessage(
        code = code,
        message = errorMessage.orEmpty(),
        errorFieldList = errorFieldList ?: emptyList()
    )
}

// create default error response
fun getDefaultErrorResponse() = ErrorResponse("", "", emptyList())

// getting error response from error body "string"
fun getErrorResponse(gson: Gson, errorBodyString: String): ErrorResponse =
    try {
        gson.fromJson(errorBodyString, ErrorResponse::class.java)
    } catch (e: Exception) {
        getDefaultErrorResponse()
    }