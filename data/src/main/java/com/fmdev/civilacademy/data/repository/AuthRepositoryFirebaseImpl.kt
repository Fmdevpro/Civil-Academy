package com.fmdev.civilacademy.data.repository

import com.fmdev.civilacademy.data.local.datasource.SessionLocalDataSource
import com.fmdev.civilacademy.data.remote.datasource.AuthRemoteDataSource
import com.fmdev.civilacademy.domain.errors.LoginError
import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.domain.repository.AuthRepository
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryFirebaseImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionLocalDataSource: SessionLocalDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): DataResult<User, DataError> {
        return when (val result = authRemoteDataSource.login(email, password)) {
            is DataResult.Success -> {
                val user = result.data
                if (user.isEmailVerified) {
                    sessionLocalDataSource.saveUser(user)
                    DataResult.Success(user)
                } else DataResult.Error(LoginError.EmailNotVerified)
            }
            is DataResult.Error -> result
            is DataResult.Loading -> result
        }
    }

    override suspend fun signInWithGoogle(idToken: String): DataResult<User, DataError> {
        return when (val result = authRemoteDataSource.signInWithGoogle(idToken)) {
            is DataResult.Success -> {
                val user = result.data
                sessionLocalDataSource.saveUser(user)
                DataResult.Success(user)
            }
            is DataResult.Error -> result
            is DataResult.Loading -> result
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): DataResult<Any, DataError> {
        return authRemoteDataSource.sendPasswordResetEmail(email)
    }

    override suspend fun sendUserEmailVerification(): DataResult<Any, DataError> {
        return authRemoteDataSource.sendUserEmailVerification()
    }

    override suspend fun isEmailVerified(): DataResult<Any, DataError> {
        return authRemoteDataSource.isEmailVerified()
    }

    override suspend fun signOut() {
        sessionLocalDataSource.clearUser()
        authRemoteDataSource.signOut()
    }

    override fun getCurrentUser(): Flow<User?> {
        return sessionLocalDataSource.getUser()
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return sessionLocalDataSource.isUserLoggedIn()
    }
}