package com.merteroglu286.domain.usecase

import com.merteroglu286.domain.model.ErrorMessage
import com.merteroglu286.domain.result.OutCome

interface UseCase<R> {

    suspend fun onSuccess(success: OutCome.Success<R>)

    suspend fun onEmpty()

    suspend fun onError(errorMessage: ErrorMessage)
}