package com.fmdev.civilacademy.data.remote.datasource

import com.fmdev.civilacademy.data.remote.helper.suspendFirebaseCall
import com.fmdev.civilacademy.data.remote.mapper.mapFirebaseException
import com.fmdev.civilacademy.domain.errors.GoogleSignInError
import com.fmdev.civilacademy.domain.errors.LoginError
import com.fmdev.civilacademy.domain.errors.ResetPasswordError
import com.fmdev.civilacademy.domain.errors.SendUserEmailVerificationError
import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.shared.constant.StringConstants.EMPTY_STRING
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRemoteDataSource {

    override suspend fun login(email: String, password: String): DataResult<User, DataError> {
        return suspendFirebaseCall(
            taskProvider = { auth.signInWithEmailAndPassword(email, password) },
            onSuccess = {
                val user = auth.currentUser
                User(
                    uid = user?.uid ?: EMPTY_STRING,
                    displayName = user?.displayName ?: EMPTY_STRING,
                    email = user?.email ?: EMPTY_STRING,
                    isEmailVerified = user?.isEmailVerified ?: false,
                    isGoogleUser = false
                )
            },
            onError = {
                mapFirebaseException(it, LoginError.UnknownError)
            }
        )
    }

    override suspend fun signInWithGoogle(idToken: String): DataResult<User, DataError> {
        if (idToken.isBlank()) return DataResult.Error(GoogleSignInError.EmptyIdToken)

        return suspendFirebaseCall(
            taskProvider = { auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)) },
            onSuccess = {
                val user = auth.currentUser
                User(
                    uid = user?.uid ?: EMPTY_STRING,
                    displayName = user?.displayName ?: EMPTY_STRING,
                    email = user?.email ?: EMPTY_STRING,
                    isEmailVerified = user?.isEmailVerified ?: false,
                    isGoogleUser = true
                )
            },
            onError = {
                mapFirebaseException(it, GoogleSignInError.UnknownError)
            }
        )
    }

    override suspend fun sendPasswordResetEmail(email: String): DataResult<Any, DataError> {
        return suspendFirebaseCall(
            taskProvider = { auth.sendPasswordResetEmail(email) },
            onSuccess = { },
            onError = {
                mapFirebaseException(it, ResetPasswordError.UnknownError)
            }
        )
    }

    override suspend fun sendUserEmailVerification(): DataResult<Any, DataError> {
        val user = auth.currentUser
            ?: return DataResult.Error(SendUserEmailVerificationError.UserNotLoggedIn)

        return suspendFirebaseCall(
            taskProvider = { user.sendEmailVerification() },
            onSuccess = { },
            onError = {
                mapFirebaseException(it, SendUserEmailVerificationError.UnknownError)
            }
        )
    }

    override suspend fun isEmailVerified(): DataResult<Any, DataError> {
        return if (auth.currentUser?.isEmailVerified == true) DataResult.Success(Unit)
        else DataResult.Error(LoginError.EmailNotVerified)
    }

    override fun signOut() {
        auth.signOut()
    }
}
