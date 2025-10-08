package com.fmdev.civilacademy.domain.errors

import com.fmdev.civilacademy.shared.model.DataError

sealed class GoogleSignInError(override val message: String? = null): DataError {
    data object EmptyIdToken: GoogleSignInError()
    data object UnknownError: GoogleSignInError()
}