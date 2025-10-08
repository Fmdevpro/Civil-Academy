package com.fmdev.civilacademy.data.remote.datasource

import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult

interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): DataResult<User, DataError>
    suspend fun signInWithGoogle(idToken: String): DataResult<User, DataError>
    suspend fun sendPasswordResetEmail(email: String): DataResult<Any, DataError>
    suspend fun isEmailVerified(): DataResult<Any, DataError>
    suspend fun sendUserEmailVerification(): DataResult<Any, DataError>
    fun signOut()
}
