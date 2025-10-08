package com.fmdev.civilacademy.di

import com.fmdev.civilacademy.data.local.datasource.SessionLocalDataSource
import com.fmdev.civilacademy.data.local.datasource.SessionLocalDataSourceImpl
import com.fmdev.civilacademy.data.remote.datasource.AuthRemoteDataSource
import com.fmdev.civilacademy.data.remote.datasource.AuthRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindSessionLocalDataSource(
        sessionLocalDataSourceImpl: SessionLocalDataSourceImpl
    ): SessionLocalDataSource
}
