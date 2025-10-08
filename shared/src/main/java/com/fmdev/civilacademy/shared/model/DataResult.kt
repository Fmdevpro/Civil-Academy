package com.fmdev.civilacademy.shared.model

sealed class DataResult<out T, out E : DataError> {
    data class Success<out T>(val data: T) : DataResult<T, Nothing>()
    data class Error<out E : DataError>(val dataError: E) : DataResult<Nothing, E>()
    data object Loading : DataResult<Nothing, Nothing>()
}

interface DataError {
    val message: String? get()= null
}