package com.fmdev.civilacademy.domain.errors

import com.fmdev.civilacademy.shared.model.DataError

sealed class ResetPasswordError(): DataError {
    data object UnknownError : ResetPasswordError()
    data object EmptyEmail : ResetPasswordError()
}