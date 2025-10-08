package com.fmdev.civilacademy.di

import com.fmdev.civilacademy.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleModule {
    @Provides
    @Singleton
    fun provideWebClientId(): String = BuildConfig.WEB_CLIENT_ID
}