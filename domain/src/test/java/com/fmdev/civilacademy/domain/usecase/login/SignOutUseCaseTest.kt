package com.fmdev.civilacademy.domain.usecase.login

import com.fmdev.civilacademy.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class SignOutUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var signOutUseCase: SignOutUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        signOutUseCase = SignOutUseCase(
            authRepository
        )
    }

    @Test
    fun `invoke should clear session and sign out from auth repository`() = runTest {
        // When
        signOutUseCase()

        // Then
        verify(authRepository).signOut()
        verifyNoMoreInteractions(authRepository)
    }
}