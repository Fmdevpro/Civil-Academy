package com.fmdev.civilacademy.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.fmdev.civilacademy.data.local.serializer.UserSerializer
import com.fmdev.civilacademy.domain.model.User
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideUserDataStore(
        @ApplicationContext context: Context
    ): DataStore<User?> {
        return DataStoreFactory.create(
            serializer = UserSerializer,
            produceFile = { context.dataStoreFile("user_session.pb") }
        )
    }
}
