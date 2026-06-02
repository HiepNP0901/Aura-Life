package com.drs.auralife.domain.result

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable? = null, val message: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

val <T> Result<T>.data: T?
    get() = when (this) {
        is Result.Success -> data
        is Result.Error -> null
        is Result.Loading -> null
    }

val Result<*>.errorMessage: String?
    get() = when (this) {
        is Result.Error -> message ?: exception?.localizedMessage
        else -> null
    }

fun <T> Result<T>.getOrDefault(default: T): T = when (this) {
    is Result.Success -> data
    else -> default
}

fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
    is Result.Loading -> this
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (Throwable?, String?) -> Unit): Result<T> {
    if (this is Result.Error) action(exception, message)
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}