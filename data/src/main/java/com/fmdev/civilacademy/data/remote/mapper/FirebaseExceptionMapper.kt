package com.fmdev.civilacademy.data.remote.mapper

import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.shared.model.DataError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

fun mapFirebaseException(exception: Exception?, defaultError: DataError): DataError {
    return when (exception) {
        is FirebaseAuthInvalidUserException -> FirebaseError.InvalidUser
        is FirebaseAuthInvalidCredentialsException -> FirebaseError.InvalidCredentials
        is FirebaseAuthUserCollisionException -> FirebaseError.UserCollision
        is FirebaseNetworkException -> FirebaseError.NetworkError
        is FirebaseAuthException -> defaultError
        else -> defaultError
    }
}