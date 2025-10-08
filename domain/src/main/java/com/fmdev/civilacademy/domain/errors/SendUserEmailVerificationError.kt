package com.fmdev.civilacademy.domain.errors

import com.fmdev.civilacademy.shared.model.DataError

sealed class SendUserEmailVerificationError(): DataError {
    object UserNotLoggedIn: SendUserEmailVerificationError()
    object EmptyEmail: SendUserEmailVerificationError()
    object UnknownError: SendUserEmailVerificationError()
}