package com.fmdev.civilacademy.di

import javax.inject.Qualifier

// Coroutine Dispatchers

@Qualifier @Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier @Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier @Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher