package com.fmdev.civilacademy.domain.usecase.login

import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.domain.errors.LoginError
import com.fmdev.civilacademy.domain.errors.ResetPasswordError
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

class ResetPasswordUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        resetPasswordUseCase = ResetPasswordUseCase(authRepository)
    }

    @Test
    fun `invoke with valid email should return Success`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(authRepository.sendPasswordResetEmail(email))
            .thenReturn(DataResult.Success(Unit))

        // When
        val result = resetPasswordUseCase(email)

        // Then
        assertTrue(result is DataResult.Success)
        verify(authRepository).sendPasswordResetEmail(email)
    }

    @Test
    fun `invoke with unregistered email should return EmailNotRegistered error`() = runTest {
        // Given
        val email = "unregistered@example.com"
        whenever(authRepository.sendPasswordResetEmail(email))
            .thenReturn(DataResult.Error(LoginError.EmailNotRegistered))

        // When
        val result = resetPasswordUseCase(email)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is LoginError.EmailNotRegistered)
        verify(authRepository).sendPasswordResetEmail(email)
    }

    @Test
    fun `invoke with network error should return NetworkError`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(authRepository.sendPasswordResetEmail(email))
            .thenReturn(DataResult.Error(FirebaseError.NetworkError))

        // When
        val result = resetPasswordUseCase(email)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is FirebaseError.NetworkError)
        verify(authRepository).sendPasswordResetEmail(email)
    }

    @Test
    fun `invoke with empty email should return EmptyEmail error`() = runTest {
        // Given
        val email = ""
        whenever(authRepository.sendPasswordResetEmail(email))
            .thenReturn(DataResult.Error(ResetPasswordError.EmptyEmail))

        // When
        val result = resetPasswordUseCase(email)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is ResetPasswordError.EmptyEmail)
    }

    @Test
    fun `invoke with unknown error should return UnknownError`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(authRepository.sendPasswordResetEmail(email))
             .thenReturn(DataResult.Error(ResetPasswordError.UnknownError))

        // When
        val result = resetPasswordUseCase(email)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is ResetPasswordError.UnknownError)
        verify(authRepository).sendPasswordResetEmail(email)
    }
}