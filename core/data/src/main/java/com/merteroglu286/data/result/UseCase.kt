package com.merteroglu286.data.result

import com.merteroglu286.data.model.ErrorMessage

interface UseCase<R> {

    suspend fun onSuccess(success: OutCome.Success<R>)

    suspend fun onEmpty()

    suspend fun onError(errorMessage: ErrorMessage)
}