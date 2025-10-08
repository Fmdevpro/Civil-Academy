package com.fmdev.civilacademy.domain.repository

import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): DataResult<User, DataError>
    suspend fun signInWithGoogle(idToken: String): DataResult<User, DataError>
    suspend fun sendPasswordResetEmail(email: String): DataResult<Any, DataError>
    suspend fun isEmailVerified(): DataResult<Any, DataError>
    suspend fun sendUserEmailVerification(): DataResult<Any, DataError>
    suspend fun signOut()
    fun getCurrentUser(): Flow<User?>
    fun isUserLoggedIn(): Flow<Boolean>
}