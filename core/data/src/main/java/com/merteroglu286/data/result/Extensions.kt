package com.merteroglu286.data.result

import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

suspend fun <T> OutCome<T>.doOnSuccess(onSuccess: suspend (T) -> Unit): OutCome<T> {
    if (this is OutCome.Success<T>) {
        if (coroutineContext.isActive) {
            onSuccess(this.data)
        }
    }
    return this
}

suspend fun <T> OutCome<T>.doOnError(onError: () -> Unit): OutCome<T> {
    if (!this.isSuccess() && coroutineContext.isActive) {
        onError()
    }
    return this
}

suspend fun <T> OutCome<T>.doOnEmpty(onEmpty: suspend () -> Unit): OutCome<T> {
    if (this is OutCome.Empty) {
        if (coroutineContext.isActive) {
            onEmpty()
        }
    }
    return this
}

suspend fun <T, R> OutCome<T>.map(mapper: suspend (T) -> R): OutCome<R> {
    return when (this) {
        is OutCome.Success<T> -> OutCome.success(mapper(this.data))
        is OutCome.Error<T> -> OutCome.error(this.errorMessage())
        is OutCome.Empty<T> -> OutCome.empty()
    }
}

suspend fun <F, S, R> OutCome<F>.merge(
    lazy: suspend () -> OutCome<S>,
    merger: (F?, S?) -> R
): OutCome<R> {
    return when (this) {
        is OutCome.Success<F> -> {
            when (val second = lazy()) {
                is OutCome.Success<S> -> {
                    OutCome.success(merger(this.data, second.data))
                }

                is OutCome.Error<S> -> {
                    OutCome.error(second.errorMessage())
                }

                is OutCome.Empty<S> -> {
                    OutCome.success(merger(this.data, null))
                }
            }
        }

        is OutCome.Error<F> -> {
            OutCome.error(this.errorMessage())
        }

        is OutCome.Empty<F> -> {
            when (val second = lazy()) {
                is OutCome.Success<S> -> {
                    OutCome.success(merger(null, second.data))
                }

                is OutCome.Error<S> -> {
                    OutCome.error(second.errorMessage())
                }

                is OutCome.Empty<S> -> {
                    OutCome.empty()
                }
            }
        }
    }
}