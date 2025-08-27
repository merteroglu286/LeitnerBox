package com.merteroglu286.data.mapper

import com.merteroglu286.data.response.ErrorResponse
import com.merteroglu286.domain.model.ErrorMessage

// mapping errorResponse to ErrorMessage model
fun ErrorResponse.toDomain(code: Int): ErrorMessage {
    return ErrorMessage(
        code = code,
        message = errorMessage.orEmpty(),
        errorFieldList = errorFieldList ?: emptyList()
    )
}
