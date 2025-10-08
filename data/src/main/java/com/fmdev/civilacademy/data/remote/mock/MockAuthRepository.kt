package com.fmdev.civilacademy.data.remote.mock

import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.domain.errors.LoginError
import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.domain.repository.AuthRepository
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MockAuthRepository : AuthRepository {

    private val currentUserFlow = MutableStateFlow<User?>(null)

    override suspend fun login(
        email: String,
        password: String
    ): DataResult<User, DataError> {
        return when {
            email == "test@example.com" && password == "password123" -> {
                val user = User(
                    uid = "uid123",
                    displayName = "Test User",
                    email = email,
                    isEmailVerified = true,
                    isGoogleUser = false
                )
                currentUserFlow.value = user
                DataResult.Success(user)
            }
            email == "unverified@example.com" && password == "password123" -> {
                DataResult.Error(LoginError.EmailNotVerified)
            }
            email == "disabled@example.com" && password == "password123" ->
                DataResult.Error(FirebaseError.UserDisabled)

            email == "test@example.com" && password == "wrongpassword" ->
                DataResult.Error(LoginError.WrongPassword)

            email == "notfound@example.com" ->
                DataResult.Error(LoginError.EmailNotRegistered)

            email.isEmpty() ->
                DataResult.Error(LoginError.EmptyEmail)

            password.isEmpty() ->
                DataResult.Error(LoginError.EmptyPassword)

            email == "network@error.com" ->
                DataResult.Error(FirebaseError.NetworkError)

            email == "invalid@credentials.com" ->
                DataResult.Error(FirebaseError.InvalidCredentials)

            else ->
                DataResult.Error(FirebaseError.UnknownError)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): DataResult<User, DataError> {
        return if (idToken == "validGoogleToken") {
            val user = User(
                uid = "googleUid",
                displayName = "Google User",
                email = "google@example.com",
                isEmailVerified = true,
                isGoogleUser = true
            )
            currentUserFlow.value = user
            DataResult.Success(user)
        } else {
            DataResult.Error(FirebaseError.InvalidCredentials)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): DataResult<Any, DataError> {
        return when (email) {
            "exists@example.com" -> DataResult.Success(Unit)
            "notfound@example.com" -> DataResult.Error(LoginError.EmailNotRegistered)
            else -> DataResult.Error(FirebaseError.UnknownError)
        }
    }

    override suspend fun isEmailVerified(): DataResult<Any, DataError> {
        return if (currentUserFlow.value?.isEmailVerified == true)
            DataResult.Success(Unit)
        else
            DataResult.Error(LoginError.EmailNotVerified)
    }

    override suspend fun sendUserEmailVerification(): DataResult<Any, DataError> {
        return if (currentUserFlow.value != null)
            DataResult.Success(Unit)
        else
            DataResult.Error(FirebaseError.InvalidUser)
    }

    override suspend fun signOut() {
        currentUserFlow.value = null
    }

    override fun getCurrentUser(): Flow<User?> {
        return currentUserFlow
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return currentUserFlow.map { it != null }
    }
}