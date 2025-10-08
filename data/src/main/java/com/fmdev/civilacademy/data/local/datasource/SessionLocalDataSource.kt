package com.fmdev.civilacademy.data.local.datasource

import com.fmdev.civilacademy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun saveUser(user: User)
    fun getUser(): Flow<User?>
    suspend fun clearUser()
    fun isUserLoggedIn(): Flow<Boolean>
}
