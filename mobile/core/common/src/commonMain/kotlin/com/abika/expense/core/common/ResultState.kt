package com.abika.expense.core.common

sealed interface ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>

    data class Error(val error: AppError) : ResultState<Nothing>
}

inline fun <T, R> ResultState<T>.map(transform: (T) -> R): ResultState<R> = when (this) {
    is ResultState.Success -> ResultState.Success(transform(data))
    is ResultState.Error -> this
}
