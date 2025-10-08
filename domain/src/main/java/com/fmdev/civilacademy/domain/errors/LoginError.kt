package com.fmdev.civilacademy.domain.errors

import com.fmdev.civilacademy.shared.model.DataError

sealed class LoginError(override val message: String? = null): DataError {
    data object UnknownError : LoginError()
    data object EmptyEmail: LoginError()
    data object EmptyPassword : LoginError()
    data object EmailNotVerified : LoginError()
    data object EmailNotRegistered : LoginError()
    data object WrongPassword : LoginError()
    data object NetworkError : LoginError()
}