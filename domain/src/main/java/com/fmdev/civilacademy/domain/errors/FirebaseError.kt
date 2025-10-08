package com.fmdev.civilacademy.domain.errors

import com.fmdev.civilacademy.shared.model.DataError

sealed class FirebaseError(): DataError {
    data object NetworkError: FirebaseError()
    data object InvalidUser: FirebaseError()
    data object InvalidCredentials: FirebaseError()
    data object UserCollision: FirebaseError()
    data object UserDisabled: FirebaseError()
    data object TooManyRequests: FirebaseError()
    data object UnknownError: FirebaseError()
}