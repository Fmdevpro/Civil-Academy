package com.fmdev.civilacademy.data.local.datasource

import androidx.datastore.core.DataStore
import com.fmdev.civilacademy.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionLocalDataSourceImpl @Inject constructor(
    private val userDataStore: DataStore<User?>
) : SessionLocalDataSource {

    override suspend fun saveUser(user: User) {
        userDataStore.updateData { user }
    }

    override fun getUser(): Flow<User?> {
        return userDataStore.data
    }

    override suspend fun clearUser() {
        userDataStore.updateData { null }
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return userDataStore.data.map { user -> user != null }
    }
}