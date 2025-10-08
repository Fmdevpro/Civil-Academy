package com.fmdev.civilacademy.di

import android.content.Context
import com.fmdev.civilacademy.androidshared.provider.GoogleSignInClientProvider
import com.fmdev.civilacademy.androidshared.provider.GoogleSignInResultHandler
import com.fmdev.civilacademy.data.remote.GoogleSignInClientProviderImpl
import com.fmdev.civilacademy.data.remote.GoogleSignInResultHandlerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleSignInModule {
    @Provides
    @Singleton
    fun provideGoogleSignInClientProvider(
        @ApplicationContext context: Context,
        webClientId: String
    ): GoogleSignInClientProvider =
        GoogleSignInClientProviderImpl(context, webClientId)

    @Provides
    @Singleton
    fun provideGoogleSignInResultHandler(): GoogleSignInResultHandler =
        GoogleSignInResultHandlerImpl()
}