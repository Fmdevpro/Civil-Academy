package com.fmdev.civilacademy.di

import com.fmdev.civilacademy.data.remote.mock.MockAuthRepository
import com.fmdev.civilacademy.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestModule {

    @Provides
    @Singleton
    fun provideMockAuthRepository(): AuthRepository = MockAuthRepository()
}
