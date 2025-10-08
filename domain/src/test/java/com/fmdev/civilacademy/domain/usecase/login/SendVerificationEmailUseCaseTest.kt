package com.fmdev.civilacademy.domain.usecase.login

import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.domain.errors.SendUserEmailVerificationError
import com.fmdev.civilacademy.domain.repository.AuthRepository
import com.fmdev.civilacademy.shared.model.DataResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

class SendVerificationEmailUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var sendVerificationEmailUseCase: SendVerificationEmailUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        sendVerificationEmailUseCase = SendVerificationEmailUseCase(authRepository)
    }

    @Test
    fun `invoke should return Success when email verification is sent`() = runTest {
        // Given
        whenever(authRepository.sendUserEmailVerification())
            .thenReturn(DataResult.Success(Unit))

        // When
        val result = sendVerificationEmailUseCase()

        // Then
        assertTrue(result is DataResult.Success)
        verify(authRepository).sendUserEmailVerification()
    }

    @Test
    fun `invoke should return UserNotFound error if no user is logged in`() = runTest {
        // Given
        whenever(authRepository.sendUserEmailVerification())
            .thenReturn(DataResult.Error(FirebaseError.InvalidUser))

        // When
        val result = sendVerificationEmailUseCase()

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is FirebaseError.InvalidUser)
        verify(authRepository).sendUserEmailVerification()
    }

    @Test
    fun `invoke should return NetworkError on network issues`() = runTest {
        // Given
        whenever(authRepository.sendUserEmailVerification())
            .thenReturn(DataResult.Error(FirebaseError.NetworkError))

        // When
        val result = sendVerificationEmailUseCase()

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is FirebaseError.NetworkError)
        verify(authRepository).sendUserEmailVerification()
    }

    @Test
    fun `invoke should return UserNotLoggedIn error when user is not logged in`() = runTest {
        // Given
        whenever(authRepository.sendUserEmailVerification())
            .thenReturn(DataResult.Error(SendUserEmailVerificationError.UserNotLoggedIn))

        // When
        val result = sendVerificationEmailUseCase()

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is SendUserEmailVerificationError.UserNotLoggedIn)
        verify(authRepository).sendUserEmailVerification()
        }
    @Test
    fun `invoke should return EmptyEmail error when email is empty`() = runTest {
        // Given
        whenever(authRepository.sendUserEmailVerification())
            .thenReturn(DataResult.Error(SendUserEmailVerificationError.EmptyEmail))

        // When
        val result = sendVerificationEmailUseCase()

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is SendUserEmailVerificationError.EmptyEmail)
        verify(authRepository).sendUserEmailVerification()
    }

    @Test
    fun `invoke should return UnknownError for an unknown error`() = runTest {
        // Given
        whenever(authRepository.sendUserEmailVerification())
            .thenReturn(DataResult.Error(SendUserEmailVerificationError.UnknownError))

        // When
        val result = sendVerificationEmailUseCase()

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is SendUserEmailVerificationError.UnknownError)
        verify(authRepository).sendUserEmailVerification()
    }
}