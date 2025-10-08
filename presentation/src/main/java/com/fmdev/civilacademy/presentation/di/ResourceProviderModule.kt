package com.fmdev.civilacademy.presentation.di

import com.fmdev.civilacademy.presentation.utils.provider.ResourceProviderImpl
import com.fmdev.civilacademy.shared.provider.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceProviderModule {

    @Binds
    abstract fun bindResourceProvider(
        resourceProviderImpl: ResourceProviderImpl
    ): ResourceProvider
}